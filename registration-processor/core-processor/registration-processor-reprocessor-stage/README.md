### registration-processor-reprocessor-stage
[Background & design]()

[Detailed Process flow]()

This stage reprocesses the packets which are stuck in Registration-processor due to failures based on the ''.

##### Default Port and Context-Path
```
eventbus.port=5750
```
##### Configurable properties from Configuration Server
```
registration.processor.reprocess.fetchsize=100
registration.processor.reprocess.elapse.time=600
registration.processor.reprocess.attempt.count=3
registration.processor.reprocess.type=cron
registration.processor.reprocess.seconds=0
registration.processor.reprocess.minutes=0/15
registration.processor.reprocess.hours=*
registration.processor.reprocess.days_of_month=*
registration.processor.reprocess.months=*
registration.processor.reprocess.days_of_week=*
```
