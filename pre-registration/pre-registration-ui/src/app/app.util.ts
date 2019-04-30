import { DatePipe, registerLocaleData } from '@angular/common';
import * as appConstants from './app.constants';
import localeFr from '@angular/common/locales/fr';
import localeAr from '@angular/common/locales/ar';
import localeEn from '@angular/common/locales/en';

export default class Utils {
  static getCurrentDate() {
    const now = new Date();
    const pipe = new DatePipe('en-US');
    let formattedDate = pipe.transform(now, 'yyyy-MM-ddTHH:mm:ss.SSS');
    formattedDate = formattedDate + 'Z';
    return formattedDate;
  }

  static getURL(currentURL: string, nextRoute: string, numberofPop = 1) {
    if (currentURL) {
      const urlSegments = currentURL.split('/');
      for (let index = 0; index < numberofPop; index++) {
        urlSegments.pop();
      }
      urlSegments.push(nextRoute);
      const url = urlSegments.join('/');
      return url;
    }
  }

  static getBookingDateTime(appointment_date: string, time_slot_from: string, language: string) {
    registerLocaleData(localeEn, appConstants.virtual_keyboard_languages.eng);
    registerLocaleData(localeAr, appConstants.virtual_keyboard_languages.ara);
    registerLocaleData(localeFr, appConstants.virtual_keyboard_languages.fra);

    const pipe = new DatePipe(appConstants.virtual_keyboard_languages[language]);
    const date = appointment_date.split('-');
    let appointmentDateTime = date[2] + ' ' + appConstants.MONTHS[Number(date[1])] + ' ' + date[0];
    appointmentDateTime = pipe.transform(appointmentDateTime, 'MMM');
    date[1] = appointmentDateTime;
    if (language === 'ara') {
      appointmentDateTime = date.join(' ');
    } else {
      appointmentDateTime = date.reverse().join(' ');
    }
    console.log(appointment_date, appointmentDateTime);
    return appointmentDateTime;
  }

  static formatTime(time_slot_from: string) {
      const time = time_slot_from.split(':');
      const appointmentDateTime =
      (Number(time[0]) > 12 ? Number(time[0]) - 12 : Number(time[0])) +
      ':' +
      time[1] +
      (Number(time[0]) >= 12 ? ' PM' : ' AM');
    return appointmentDateTime;
  }
}
