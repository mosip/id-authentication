import { Injectable } from '@angular/core';
import { LogEntry, LogLevel } from './log.entry';
import { LogPublisher } from './log.publisher';
import { LogPublishersService } from './log.publishers.service';

// export enum LogLevel {
//   All = 0,
//   Debug = 1,
//   Info = 2,
//   Warn = 3,
//   Error = 4,
//   Fatal = 5,
//   Off = 6
// }

@Injectable({
  providedIn: 'root'
})
export class LogService {
  level: LogLevel = LogLevel.All;
  logWithDate: boolean = true;
  publishers: LogPublisher[];
  constructor(private publishersService: LogPublishersService) {
    console.log('logger construcutor');

    this.publishers = this.publishersService.publishers;
  }

  debug(msg: string, ...optionalParams: any[]) {
    this.writeToLog(msg, LogLevel.Debug, optionalParams);
  }

  info(msg: string, ...optionalParams: any[]) {
    this.writeToLog(msg, LogLevel.Info, optionalParams);
  }

  warn(msg: string, ...optionalParams: any[]) {
    this.writeToLog(msg, LogLevel.Warn, optionalParams);
  }

  error(msg: string, ...optionalParams: any[]) {
    this.writeToLog(msg, LogLevel.Error, optionalParams);
  }

  fatal(msg: string, ...optionalParams: any[]) {
    this.writeToLog(msg, LogLevel.Fatal, optionalParams);
  }

  log(msg: string, ...optionalParams: any[]) {
    this.writeToLog(msg, LogLevel.All, optionalParams);
  }

  private writeToLog(msg: string, level: LogLevel, params: any[]) {
    if (this.shouldLog(level)) {
      let entry: LogEntry = new LogEntry();
      entry.message = msg;
      entry.level = level;
      entry.extraInfo = params;
      entry.logWithDate = this.logWithDate;
      // console.log(entry.buildLogString());
      for (let logger of this.publishers) {
        logger.log(entry).subscribe(response => console.log(response));
      }
    }
  }

  private shouldLog(level: LogLevel): boolean {
    let ret: boolean = false;
    if ((level >= this.level && level !== LogLevel.Off) || this.level === LogLevel.All) {
      ret = true;
    }
    return ret;
  }
}
