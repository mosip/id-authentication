import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { UinstatusService } from '../../../shared/services/uinstatus.service';

@Component({
  selector: 'app-uin',
  templateUrl: './uin.component.html',
  styleUrls: ['./uin.component.css']
})
export class UinComponent implements OnInit {
  inputUin: String = '';
  response: any = {};
  status: String = '';
  activeMessage: String = ' is Active';
  deactiveMessage: String = ' is Deactive';



  uinStatusForm = this.formBuilder.group({ uin: ['', Validators.required] });

  constructor(private inhttp: HttpClient, private formBuilder: FormBuilder, private service: UinstatusService) {

  }

  ngOnInit() { }

  search() {
    this.service.getUinStatus(this.inputUin.trim()).subscribe((response) => {
      this.response = response;
      console.log(response);

      if (this.response.response.status === 'ACTIVATED') {
        this.status = this.inputUin + '' + this.activeMessage;
      } else if (this.response.response.status === 'DEACTIVATED') {
        this.status = this.inputUin + '' + this.deactiveMessage;
      }
    });

  }

  refresh() {
    this.inputUin = '';
    this.response = null;
    this.response.response = null;
    this.response.errors = null;
    this.response.errors = '';
    this.response.errors[0].message = null;
    this.response.errors[0].message = '';
  }

  clearResponse() {
    if (this.inputUin.length === 0) {
      this.response = null;
    }
  }

}
