import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-masterdata",
  templateUrl: "./masterdata.component.html",
  styleUrls: ["./masterdata.component.css"]
})
export class MasterdataComponent implements OnInit {
  constructor() {}
  searchArray = [];

  set: any;
  data = [
    {
      code: "CIN",
      name: "CNIE Card",
      description: "Rental Agreement of address",
      languagecode: "eng",
      status: "TRUE"
    },
    {
      code: "CIN",
      name: "Passport",
      description: "Rental agreement of address",
      languagecode: "eng",
      status: "TRUE"
    },
    {
      code: "CINE",
      name: "Certificate of Relationship",
      description: "Proof relationship of a person",
      languagecode: "eng",
      status: "TRUE"
    }
  ];

  tableData = this.data;

  ngOnInit() {}

}
