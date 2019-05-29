export enum LogLevel {
  All = 0,
  Debug = 1,
  Info = 2,
  Warn = 3,
  Error = 4,
  Fatal = 5,
  Off = 6
}

export class LogEntry {
  // Public Properties
  entryDate: Date = new Date();
  message: string = '';
  level: LogLevel = LogLevel.Debug;
  extraInfo: any[] = [];
  logWithDate: boolean = true;

  buildLogString(): string {
    let ret: string = '';
    if (this.logWithDate) {
      ret = new Date() + ' - ';
    }
    ret += 'Type: ' + LogLevel[this.level];
    ret += ' - Message: ' + this.message;
    if (this.extraInfo.length) {
      ret += ' - Extra Info: ' + this.formatParams(this.extraInfo);
    }
    return ret;
  }

  private formatParams(params: any[]): string {
    let ret: string = params.join(',');
    if (params.some(p => typeof p == 'object')) {
      ret = '';
      for (let item of params) {
        ret += JSON.stringify(item) + ',';
      }
    }

    return ret;
  }
}
