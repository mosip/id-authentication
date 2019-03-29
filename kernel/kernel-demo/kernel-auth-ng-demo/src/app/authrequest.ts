export class AuthRequest {
    userName: string;
    password: string;
    appId: string;
    constructor(userName: string, password: string, appId: string) {
        this.userName = userName;
        this.password = password;
        this.appId = appId;
    }
}
