import { DatePipe } from '@angular/common';

export default class Utils {
  static getCurrentDate() {
    const now = new Date();
    const pipe = new DatePipe('en-US');
    let formattedDate = pipe.transform(now, 'yyyy-MM-ddTHH:mm:ss.SSS');
    formattedDate = formattedDate + 'Z';
    return formattedDate;
  }

  static getURL(currentURL: string, nextRoute: string, numberofPop = 1) {
    const urlSegments = currentURL.split('/');
    for (let index = 0; index < numberofPop; index++) {
      urlSegments.pop();
    }
    urlSegments.push(nextRoute);
    const url = urlSegments.join('/');
    return url;
  }
}
