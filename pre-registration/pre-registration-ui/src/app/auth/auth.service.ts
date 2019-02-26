export class AuthService {
  token: string;

  setToken() {
    this.token = 'settingToken';
  }

  isAuthenticated() {
    return this.token != null;
    // if (localStorage.getItem('loggedIn') && localStorage.getItem('loggedIn') === 'true') return true;
    // else return false;
  }
}
