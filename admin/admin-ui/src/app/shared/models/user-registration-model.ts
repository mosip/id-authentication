import Utils from "../../app.util";

export interface UserRegistrationRequestModel {
    userName: string;
    firstName: string;
    lastName: string;
    contactNo: string;
    emailID: string;
    dateOfBirth: any;
    gender: string;
    role: string;
    ridValidationUrl:string;
    appId:string;
}