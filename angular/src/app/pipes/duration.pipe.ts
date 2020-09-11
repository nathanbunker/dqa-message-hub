import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'duration'
})
export class DurationPipe implements PipeTransform {

  transform(milliseconds: any, args?: any): any {
      const seconds = Math.floor(milliseconds / 1000);
      const h = 3600;
      const m = 60;
      const hours = Math.floor(seconds / h);
      const minutes = Math.floor((seconds % h) / m);
      const scnds = Math.floor((seconds % m));
      let timeString = '';
      // if (scnds < 10) {
      //   scnds = "0" + scnds;
      // }
      // if (hours < 10) {
      //   hours = "0" + hours;
      // }
      // if (minutes < 10) {
      //   minutes = "0" + minutes;
      // }

      timeString =
          (hours > 0 ? (hours + ' h ') : '') +
          (minutes > 0 ? (minutes + ' m ') : '') +
          (scnds > 0 ? (scnds + ' s') : '');

      if (!timeString) {
        return milliseconds + ' ms';
      }
      return (timeString);
  }

}
