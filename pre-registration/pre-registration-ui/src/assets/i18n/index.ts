import ara from './ara.json';
import fra from './fra.json';
import eng from './eng.json';

export default class LanguageFactory {
  currentLanguage: any;
  constructor(language: string) {
    switch (language) {
      case 'ara':
        this.currentLanguage = ara;
        break;
      case 'fra':
        this.currentLanguage = fra;
        break;
      case 'eng':
        this.currentLanguage = eng;
        break;
    }
  }

  public getCurrentlanguage() {
    return this.currentLanguage;
  }
}

// export default { ara, fra, eng };
