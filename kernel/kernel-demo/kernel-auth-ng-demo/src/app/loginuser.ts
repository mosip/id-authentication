import { AuthRequest } from './authrequest';

export class LoginUser {
    id: string;
    timestamp: any;
    ver: string;
    request: AuthRequest;
    constructor(id: string, timestamp: any, ver: string, authRequest: AuthRequest) {
        this.id = id;
        this.timestamp = timestamp;
        this.ver = ver;
        this.request = authRequest;
    }
}

