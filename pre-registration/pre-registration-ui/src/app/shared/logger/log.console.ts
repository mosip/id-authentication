import { LogPublisher } from './log.publisher';
import { LogEntry } from './log.entry';
import { Observable, of } from 'rxjs';

export class LogConsole extends LogPublisher {
  log(entry: LogEntry): Observable<boolean> {
    console.log(entry.buildLogString());
    return of(true);
  }
  clear(): Observable<boolean> {
    console.clear();
    return of(true);
  }
}
