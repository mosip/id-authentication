export class AuthService {
  isAuthenticated() {
    if (localStorage.getItem('loggedIn') && localStorage.getItem('loggedIn') === 'true')
      return true;
    else
      return false;
  }
}
