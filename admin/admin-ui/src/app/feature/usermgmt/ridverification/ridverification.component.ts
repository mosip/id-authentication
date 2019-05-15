import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { UserregistrationService } from '../../../shared/services/userregistration.service';
import { RidVeerificationRequestModel } from '../../../shared/models/rid-verification-model';
import { RequestModel } from '../../../shared/models/request-model';

@Component({
  selector: 'app-ridverification',
  templateUrl: './ridverification.component.html',
  styleUrls: ['./ridverification.component.css']
})
export class RidverificationComponent implements OnInit {
  userName: string;
  requestDTO: any;
  requestObject = {} as RidVeerificationRequestModel;
  userRidVerificationForm: FormGroup;
  constructor(private router: Router, private activatedRoute: ActivatedRoute, private formBuilder: FormBuilder, private service: UserregistrationService) {
    this.userRidVerificationForm = this.formBuilder.group({
      rid: ['', [Validators.required, Validators.pattern('^[0-9]*$')]]
    });


  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      const userNameQueryParam = params['username'];
      this.userName = userNameQueryParam;
    });
  }
  onSubmit() {
    this.requestObject.rid = this.userRidVerificationForm.get('rid').value;
    this.requestObject.userName = this.userName;
    this.requestDTO = new RequestModel('id', 'v1', this.requestObject, null);
    this.service.ridVerification(this.requestDTO).subscribe(data => {
      alert('Rid submitted successfully');
      this.router.navigateByUrl('/admin/usermgmt/validateotp?username=' + this.userName + '&rid=' + this.requestObject.rid);
    }, error => {
      console.log(error);
    });
  }
}
