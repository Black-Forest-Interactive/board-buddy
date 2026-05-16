import {Pipe, PipeTransform} from '@angular/core';
import {DateTime} from 'luxon';

@Pipe({
  name: 'timestamp'
})
export class TimestampPipe implements PipeTransform {

  transform(value: string, ...args: unknown[]): string {
    const timestamp = DateTime.fromISO(value)
    return timestamp.setLocale("de").toLocaleString(DateTime.TIME_SIMPLE)
  }

}
