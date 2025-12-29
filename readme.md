{\rtf1\ansi\ansicpg1252\cocoartf2867
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Arial-BoldMT;\f1\fswiss\fcharset0 Helvetica;\f2\fswiss\fcharset0 ArialMT;
\f3\froman\fcharset0 TimesNewRomanPSMT;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue0;\red109\green109\blue109;}
{\*\expandedcolortbl;;\cssrgb\c0\c0\c0;\cssrgb\c50196\c50196\c50196;}
{\*\listtable{\list\listtemplateid1\listhybrid{\listlevel\levelnfc23\levelnfcn23\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{disc\}}{\leveltext\leveltemplateid1\'01\uc0\u8226 ;}{\levelnumbers;}\fi-360\li720\lin720 }{\listname ;}\listid1}
{\list\listtemplateid2\listhybrid{\listlevel\levelnfc23\levelnfcn23\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{disc\}}{\leveltext\leveltemplateid101\'01\uc0\u8226 ;}{\levelnumbers;}\fi-360\li720\lin720 }{\listname ;}\listid2}
{\list\listtemplateid3\listhybrid{\listlevel\levelnfc23\levelnfcn23\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{disc\}}{\leveltext\leveltemplateid201\'01\uc0\u8226 ;}{\levelnumbers;}\fi-360\li720\lin720 }{\listname ;}\listid3}
{\list\listtemplateid4\listhybrid{\listlevel\levelnfc23\levelnfcn23\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{disc\}}{\leveltext\leveltemplateid301\'01\uc0\u8226 ;}{\levelnumbers;}\fi-360\li720\lin720 }{\listname ;}\listid4}
{\list\listtemplateid5\listhybrid{\listlevel\levelnfc23\levelnfcn23\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{disc\}}{\leveltext\leveltemplateid401\'01\uc0\u8226 ;}{\levelnumbers;}\fi-360\li720\lin720 }{\listname ;}\listid5}
{\list\listtemplateid6\listhybrid{\listlevel\levelnfc23\levelnfcn23\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{disc\}}{\leveltext\leveltemplateid501\'01\uc0\u8226 ;}{\levelnumbers;}\fi-360\li720\lin720 }{\listname ;}\listid6}
{\list\listtemplateid7\listhybrid{\listlevel\levelnfc23\levelnfcn23\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{disc\}}{\leveltext\leveltemplateid601\'01\uc0\u8226 ;}{\levelnumbers;}\fi-360\li720\lin720 }{\listname ;}\listid7}}
{\*\listoverridetable{\listoverride\listid1\listoverridecount0\ls1}{\listoverride\listid2\listoverridecount0\ls2}{\listoverride\listid3\listoverridecount0\ls3}{\listoverride\listid4\listoverridecount0\ls4}{\listoverride\listid5\listoverridecount0\ls5}{\listoverride\listid6\listoverridecount0\ls6}{\listoverride\listid7\listoverridecount0\ls7}}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\deftab720
\pard\pardeftab720\sa213\partightenfactor0

\f0\b\fs64 \cf2 \expnd0\expndtw0\kerning0
Distributed Transaction Engine & Banking System
\f1\b0\fs32 \
\pard\pardeftab720\sa213\partightenfactor0

\f2 \cf2 A robust, thread-safe banking core implemented in Java, designed to handle concurrent transfers, scheduled payments, and high-performance transaction logging.
\f1 \
\pard\pardeftab720\sa213\partightenfactor0

\f0\b\fs48 \cf2 Key Features
\f1\b0\fs32 \
\pard\pardeftab720\sa213\partightenfactor0
\ls1\ilvl0
\f0\b \cf2 {\listtext	\uc0\u8226 	}Thread-Safe Transfers:
\f2\b0  Prevents deadlocks using ordered synchronization logic.
\f1 \
\ls1\ilvl0
\f0\b {\listtext	\uc0\u8226 	}Scheduled Execution:
\f2\b0  A priority-based task engine for future-dated payments.
\f1 \
\ls1\ilvl0
\f0\b {\listtext	\uc0\u8226 	}High-Performance Ledger:
\f2\b0  Optimized for $O(1)$ write-heavy transaction logging.
\f1 \
\ls1\ilvl0
\f0\b {\listtext	\uc0\u8226 	}Advanced Analytics:
\f2\b0  Efficiently calculates "Top Spenders" using a Min-Heap strategy.
\f1 \
\pard\pardeftab720\sa160\qc\partightenfactor0

\f3 \cf3 \
\pard\pardeftab720\partightenfactor0

\f0\b\fs48 \cf2 Architectural Design Decisions
\f1\b0\fs32 \
\pard\pardeftab720\partightenfactor0

\f2 \cf2 \'a0
\f1 \
\pard\pardeftab720\sa213\partightenfactor0

\f0\b\fs36 \cf2 1. Concurrency Control: Ordered Locking
\f1\b0\fs32 \
\pard\pardeftab720\sa213\partightenfactor0

\f2 \cf2 In a banking system, the primary risk is the 
\f0\b Circular Wait
\f2\b0  deadlock (e.g., User A transfers to B while User B transfers to A simultaneously).
\f1 \
\pard\pardeftab720\sa213\partightenfactor0
\ls2\ilvl0
\f0\b \cf2 {\listtext	\uc0\u8226 	}The Strategy:
\f2\b0  Instead of a global lock which would bottleneck the entire bank, I implemented 
\f0\b Ordered Resource Locking
\f2\b0  in 
\fs26\fsmilli13333 Bank.java
\fs32 .
\f1 \
\ls2\ilvl0
\f0\b {\listtext	\uc0\u8226 	}The Implementation:
\f2\b0  By comparing the 
\fs26\fsmilli13333 hashCode()
\fs32  of the two accounts and always acquiring the lock for the "lower" account first, the system guarantees a consistent locking order, effectively making deadlocks mathematically impossible.
\f1 \
\pard\pardeftab720\sa213\partightenfactor0

\f0\b\fs36 \cf2 2. Transaction Ledger & Data Segregation
\f1\b0\fs32 \
\pard\pardeftab720\sa213\partightenfactor0
\ls3\ilvl0
\f0\b \cf2 {\listtext	\uc0\u8226 	}Design:
\f2\b0  I utilized a 
\fs26\fsmilli13333 Map<String, List<LogEntry>>
\fs32  where the key is the Account ID.
\f1 \
\ls3\ilvl0
\f0\b {\listtext	\uc0\u8226 	}Why:
\f2\b0  A single global list of transactions would suffer from massive contention in a multi-threaded environment. Segregating logs by account allows for faster history lookups ($O(1)$ to find the account's history) and better cache locality.
\f1 \
\pard\pardeftab720\sa213\partightenfactor0

\f0\b\fs36 \cf2 3. Execution Engine (Scheduled Tasks)
\f1\b0\fs32 \
\pard\pardeftab720\sa213\partightenfactor0
\ls4\ilvl0
\f0\b \cf2 {\listtext	\uc0\u8226 	}The Problem:
\f2\b0  How to handle thousands of future payments without constantly polling the entire database.
\f1 \
\ls4\ilvl0
\f0\b {\listtext	\uc0\u8226 	}The Solution:
\f2\b0  I implemented a 
\fs26\fsmilli13333 PriorityQueue
\fs32  (Min-Heap) in the 
\fs26\fsmilli13333 Bank
\fs32  class.
\f1 \
\ls4\ilvl0
\f0\b {\listtext	\uc0\u8226 	}Performance:
\f2\b0  This ensures that 
\fs26\fsmilli13333 processScheduledTasks
\fs32  only ever checks the most "urgent" task. The system achieves $O(1)$ access to the next task due and $O(\\log N)$ for inserting new scheduled payments.
\f1 \
\pard\pardeftab720\sa213\partightenfactor0

\f0\b\fs36 \cf2 4. Top Spenders (Heap-Based Filtering)
\f1\b0\fs32 \
\pard\pardeftab720\sa213\partightenfactor0
\ls5\ilvl0
\f0\b \cf2 {\listtext	\uc0\u8226 	}Optimization:
\f2\b0  When calculating the top $N$ spenders, many developers sort the entire list ($O(N \\log N)$).
\f1 \
\ls5\ilvl0
\f0\b {\listtext	\uc0\u8226 	}Decision:
\f2\b0  I implemented a 
\f0\b Min-Heap
\f2\b0  of size $n$. This keeps the time complexity down to $O(N \\log n)$, which is significantly faster when the total number of users ($N$) is much larger than the requested top list ($n$).
\f1 \
\pard\pardeftab720\sa160\qc\partightenfactor0

\f3 \cf3 \
\pard\pardeftab720\sa213\partightenfactor0

\f0\b\fs48 \cf2 \'a0Project Structure
\f1\b0\fs32 \
\pard\pardeftab720\sa213\partightenfactor0
\ls6\ilvl0
\f2\fs26\fsmilli13333 \cf2 {\listtext	\uc0\u8226 	}Bank.java
\fs32 : The core engine containing the transfer logic and task scheduler.
\f1 \
\ls6\ilvl0
\f2\fs26\fsmilli13333 {\listtext	\uc0\u8226 	}Account.java
\fs32 : Represents the financial state; balances are managed with precision.
\f1 \
\ls6\ilvl0
\f2\fs26\fsmilli13333 {\listtext	\uc0\u8226 	}User.java
\fs32 : Implemented via the Builder Pattern to ensure immutability and valid state upon creation.
\f1 \
\ls6\ilvl0
\f2\fs26\fsmilli13333 {\listtext	\uc0\u8226 	}TransactionLedger.java
\fs32 : Manages thread-safe storage and retrieval of historical records.
\f1 \
\ls6\ilvl0
\f2\fs26\fsmilli13333 {\listtext	\uc0\u8226 	}ScheduledTask.java
\fs32 : A DTO representing a future financial obligation.
\f1 \
\pard\pardeftab720\sa160\qc\partightenfactor0

\f3 \cf3 \
\pard\pardeftab720\sa213\partightenfactor0

\f0\b\fs48 \cf2 Future Enhancements
\f1\b0\fs32 \
\pard\pardeftab720\sa213\partightenfactor0
\ls7\ilvl0
\f0\b \cf2 {\listtext	\uc0\u8226 	}Persistence:
\f2\b0  Integrating a JPA/Hibernate layer to move from in-memory maps to a relational database.
\f1 \
\ls7\ilvl0
\f0\b {\listtext	\uc0\u8226 	}AtomicLong Migration:
\f2\b0  For even higher throughput on single-account balance checks, migrating 
\fs26\fsmilli13333 balance
\fs32  to 
\fs26\fsmilli13333 AtomicLong
\fs32  to utilize CAS (Compare-And-Swap) operations.
\f1 \
\ls7\ilvl0
\f0\b {\listtext	\uc0\u8226 	}Distributed Locking:
\f2\b0  Implementing Redis-based Redlock to support scaling this system across multiple server instances.
\f1 \
\pard\pardeftab720\sl368\sa213\partightenfactor0
\cf2 \'a0\
}