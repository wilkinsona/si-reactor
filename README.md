si-reactor
==========

Playground for combining SI and Reator/LMAX Distruptor

Performance comparison
----------------------

| Type                                 | Same message throughput (msg/sec) | New message throughput (msg/sec) | New message w/ custom id throughput (msg/sec) |
| -----------------------------------: | --------------------------------- | -------------------------------- | --------------------------------------------- |
| Direct channel                       | 12820512                          | 441696                           | 2980625                                       |
| Ring buffer channel                  | 6779661                           | 330742                           | 2068252                                       |
| Spring executor                      | 1554001                           | 329109                           | 1992031                                       |
| Reactor fixed thread pool executor   | 1808318                           | 281848                           | 1266624                                       |
| Reactor ring buffer executor         | 1110494                           | 314613                           | 2089864                                       |