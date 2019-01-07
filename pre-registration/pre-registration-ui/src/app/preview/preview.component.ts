import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {

  previewData = [
    {
      Language: 'English',
      data : [
        {
          key: 'Personal Information',
          data: [
            {
              key: 'Full Name',
              value: 'Deepak Sharma'
            },
            {
              key: 'DOB',
              value: '22/02/1991'
            },
            {
              key: 'Age',
              value: '40 Years'
            },
            {
              key: 'Gender',
              value: 'Male'
            }
          ]
        },
        {
          key: 'Contact Details',
          data:[
            {
              key: 'Address',
              value: '877, 28th Main, Corporation Colony 9th Block Jayanagar'
            },
            {
              key: 'Region',
              value: 'North Bangalore'
            },
            {
              key: 'Province',
              value: 'Karnataka'
            },
            {
              key: 'City',
              value: 'Bangalore'
            },
            {
              key: 'Local adminstrative authority',
              value: 'Bangalore'
            },
            {
              key: 'Mobile Number',
              value: 9876543210
            },
            {
              key: 'Alternative Mobile Number',
              value: 8899009988
            },
            {
              key: 'Email ID',
              value: 'example@mail.com'
            },
            {
              key: 'CINE/PIN Number',
              value: 43345233
            }
          ]
        }
      ]
    },
    {
      Language: 'English',
      data : [
        {
          key: 'Personal Information',
          data: [
            {
              key: 'Full Name',
              value: 'Deepak Sharma'
            },
            {
              key: 'DOB',
              value: '22/02/1991'
            },
            {
              key: 'Age',
              value: '40 Years'
            },
            {
              key: 'Gender',
              value: 'Male'
            }
          ]
        },
        {
          key: 'Contact Details',
          data:[
            {
              key: 'Address',
              value: '877, 28th Main, Corporation Colony 9th Block Jayanagar'
            },
            {
              key: 'Region',
              value: 'North Bangalore'
            },
            {
              key: 'Province',
              value: 'Karnataka'
            },
            {
              key: 'City',
              value: 'Bangalore'
            },
            {
              key: 'Local adminstrative authority',
              value: 'Bangalore'
            },
            {
              key: 'Mobile Number',
              value: 9876543210
            },
            {
              key: 'Alternative Mobile Number',
              value: 8899009988
            },
            {
              key: 'Email ID',
              value: 'example@mail.com'
            },
            {
              key: 'CINE/PIN Number',
              value: 43345233
            }
          ]
        }
      ]
    }
  ];

  constructor() { }

  ngOnInit() {
  }

}
