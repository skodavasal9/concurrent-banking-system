Distributed Transaction Engine & Banking System
A robust, thread-safe banking core implemented in Java, designed to handle concurrent transfers, scheduled payments, and high-performance transaction logging.
Key Features
	•	Thread-Safe Transfers: Prevents deadlocks using ordered synchronization logic.
	•	Scheduled Execution: A priority-based task engine for future-dated payments.
	•	High-Performance Ledger: Optimized for $O(1)$ write-heavy transaction logging.
	•	Advanced Analytics: Efficiently calculates "Top Spenders" using a Min-Heap strategy.

Architectural Design Decisions
 
1. Concurrency Control: Ordered Locking
In a banking system, the primary risk is the Circular Wait deadlock (e.g., User A transfers to B while User B transfers to A simultaneously).
	•	The Strategy: Instead of a global lock which would bottleneck the entire bank, I implemented Ordered Resource Locking in Bank.java.
	•	The Implementation: By comparing the hashCode() of the two accounts and always acquiring the lock for the "lower" account first, the system guarantees a consistent locking order, effectively making deadlocks mathematically impossible.
2. Transaction Ledger & Data Segregation
	•	Design: I utilized a Map<String, List<LogEntry>> where the key is the Account ID.
	•	Why: A single global list of transactions would suffer from massive contention in a multi-threaded environment. Segregating logs by account allows for faster history lookups ($O(1)$ to find the account's history) and better cache locality.
3. Execution Engine (Scheduled Tasks)
	•	The Problem: How to handle thousands of future payments without constantly polling the entire database.
	•	The Solution: I implemented a PriorityQueue (Min-Heap) in the Bank class.
	•	Performance: This ensures that processScheduledTasks only ever checks the most "urgent" task. The system achieves $O(1)$ access to the next task due and $O(\log N)$ for inserting new scheduled payments.
4. Top Spenders (Heap-Based Filtering)
	•	Optimization: When calculating the top $N$ spenders, many developers sort the entire list ($O(N \log N)$).
	•	Decision: I implemented a Min-Heap of size $n$. This keeps the time complexity down to $O(N \log n)$, which is significantly faster when the total number of users ($N$) is much larger than the requested top list ($n$).

 Project Structure
	•	Bank.java: The core engine containing the transfer logic and task scheduler.
	•	Account.java: Represents the financial state; balances are managed with precision.
	•	User.java: Implemented via the Builder Pattern to ensure immutability and valid state upon creation.
	•	TransactionLedger.java: Manages thread-safe storage and retrieval of historical records.
	•	ScheduledTask.java: A DTO representing a future financial obligation.

Future Enhancements
	•	Persistence: Integrating a JPA/Hibernate layer to move from in-memory maps to a relational database.
	•	AtomicLong Migration: For even higher throughput on single-account balance checks, migrating balance to AtomicLong to utilize CAS (Compare-And-Swap) operations.
	•	Distributed Locking: Implementing Redis-based Redlock to support scaling this system across multiple server instances.
 
