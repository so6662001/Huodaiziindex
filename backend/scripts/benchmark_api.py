#!/usr/bin/env python3
import argparse
import json
import math
import statistics
import threading
import time
import urllib.error
import urllib.parse
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime, timezone


def build_request(method, url, headers=None, body=None):
    req = urllib.request.Request(url=url, method=method)
    if headers:
        for k, v in headers.items():
            req.add_header(k, v)
    if body is not None:
        req.data = body.encode("utf-8")
    return req


def do_request(method, url, headers=None, body=None, timeout=10.0):
    req = build_request(method, url, headers=headers, body=body)
    start = time.perf_counter()
    status = 0
    ok = False
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            status = resp.getcode()
            resp.read()
            ok = 200 <= status < 400
    except urllib.error.HTTPError as e:
        status = e.code
        ok = 200 <= status < 400
    except Exception:
        status = 0
        ok = False
    elapsed_ms = (time.perf_counter() - start) * 1000.0
    return elapsed_ms, status, ok


def percentile(sorted_values, p):
    if not sorted_values:
        return 0.0
    if len(sorted_values) == 1:
        return sorted_values[0]
    k = (len(sorted_values) - 1) * p
    f = math.floor(k)
    c = math.ceil(k)
    if f == c:
        return sorted_values[int(k)]
    d0 = sorted_values[f] * (c - k)
    d1 = sorted_values[c] * (k - f)
    return d0 + d1


def run_scenario(name, method, url, headers, body, total_requests, concurrency, timeout):
    latencies = []
    status_count = {}
    ok_count = 0
    lock = threading.Lock()
    wall_start = time.perf_counter()

    def task():
        return do_request(method, url, headers=headers, body=body, timeout=timeout)

    with ThreadPoolExecutor(max_workers=concurrency) as executor:
        futures = [executor.submit(task) for _ in range(total_requests)]
        for f in as_completed(futures):
            elapsed_ms, status, ok = f.result()
            with lock:
                latencies.append(elapsed_ms)
                status_count[str(status)] = status_count.get(str(status), 0) + 1
                if ok:
                    ok_count += 1

    wall_seconds = max(time.perf_counter() - wall_start, 1e-9)
    latencies.sort()
    avg = statistics.mean(latencies) if latencies else 0.0
    stdev = statistics.pstdev(latencies) if len(latencies) > 1 else 0.0
    tps = total_requests / wall_seconds
    error_count = total_requests - ok_count
    error_rate = error_count / total_requests if total_requests else 0.0

    return {
        "name": name,
        "method": method,
        "url": url,
        "requests": total_requests,
        "concurrency": concurrency,
        "duration_seconds": round(wall_seconds, 4),
        "throughput_rps": round(tps, 2),
        "success_count": ok_count,
        "error_count": error_count,
        "error_rate": round(error_rate, 4),
        "status_distribution": status_count,
        "latency_ms": {
            "min": round(latencies[0] if latencies else 0.0, 3),
            "avg": round(avg, 3),
            "p50": round(percentile(latencies, 0.50), 3),
            "p90": round(percentile(latencies, 0.90), 3),
            "p95": round(percentile(latencies, 0.95), 3),
            "p99": round(percentile(latencies, 0.99), 3),
            "max": round(latencies[-1] if latencies else 0.0, 3),
            "stddev": round(stdev, 3),
        },
    }


def main():
    parser = argparse.ArgumentParser(description="Huodaizi API benchmark")
    parser.add_argument("--base-url", default="http://127.0.0.1:18080", help="API base URL")
    parser.add_argument("--admin-token", default="change-this-admin-token", help="admin token")
    parser.add_argument("--requests", type=int, default=200, help="requests per scenario")
    parser.add_argument("--concurrency", type=int, default=20, help="concurrency per scenario")
    parser.add_argument("--timeout", type=float, default=10.0, help="per-request timeout seconds")
    parser.add_argument(
        "--output",
        default="backend/reports/perf_benchmark_report.json",
        help="output json file",
    )
    args = parser.parse_args()

    base = args.base_url.rstrip("/")
    submit_payload = json.dumps(
        {
            "placementId": "SAPERF001",
            "placementName": "性能压测广告位",
            "city": "唐山",
            "duration": "7天",
            "budget": "1000-2000",
            "companyName": "性能压测有限公司",
            "contactName": "压测员",
            "contactPhone": "13612345678",
            "remark": "性能基准测试",
            "agreed": True,
        },
        ensure_ascii=False,
    )
    assign_payload = json.dumps(
        {"ownerName": "性能商务", "team": "压测组", "comment": "性能压测分配"},
        ensure_ascii=False,
    )

    scenarios = [
        {
            "name": "public_mine_read",
            "method": "GET",
            "url": f"{base}/api/v1/site-ad-lead/mine?contactPhone=13800138000&page=1&pageSize=10",
            "headers": {},
            "body": None,
        },
        {
            "name": "public_submit_write",
            "method": "POST",
            "url": f"{base}/api/v1/site-ad-lead/submit",
            "headers": {"Content-Type": "application/json"},
            "body": submit_payload,
        },
        {
            "name": "admin_list_authorized",
            "method": "GET",
            "url": f"{base}/api/admin/site-ad-lead?page=1&pageSize=10",
            "headers": {"X-Admin-Token": args.admin_token},
            "body": None,
        },
        {
            "name": "admin_list_unauthorized",
            "method": "GET",
            "url": f"{base}/api/admin/site-ad-lead?page=1&pageSize=10",
            "headers": {},
            "body": None,
        },
        {
            "name": "admin_assign_authorized",
            "method": "PUT",
            "url": f"{base}/api/admin/site-ad-lead/SAL20260418001/assign",
            "headers": {
                "X-Admin-Token": args.admin_token,
                "Content-Type": "application/json",
            },
            "body": assign_payload,
        },
    ]

    started_at = datetime.now(timezone.utc).isoformat()
    results = []
    for s in scenarios:
        result = run_scenario(
            name=s["name"],
            method=s["method"],
            url=s["url"],
            headers=s["headers"],
            body=s["body"],
            total_requests=args.requests,
            concurrency=args.concurrency,
            timeout=args.timeout,
        )
        results.append(result)

    report = {
        "meta": {
            "started_at": started_at,
            "base_url": base,
            "requests_per_scenario": args.requests,
            "concurrency": args.concurrency,
            "timeout_seconds": args.timeout,
        },
        "scenarios": results,
    }

    output_path = args.output
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(report, f, ensure_ascii=False, indent=2)

    print(json.dumps(report, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
