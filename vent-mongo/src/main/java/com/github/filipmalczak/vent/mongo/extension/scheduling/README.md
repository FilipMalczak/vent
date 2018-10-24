# Scheduling API

Yet another candidate for a new module. Provides abstraction for repetitive jobs.

Short glossary:
- Scheduler - entity that does the scheduling (sets up repeating some task according to some timetable)
- ScheduleScheme - definition of how the task should be repeated, without specifying what should be repeated. Think 
"every 5 minutes" or "cron expression"
- Schedule - the fact of executing some task on according to a schedule scheme. Basically a handle on the implementation,
allowing for cancellation.