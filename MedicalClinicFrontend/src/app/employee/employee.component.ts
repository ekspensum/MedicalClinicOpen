import { Component, OnInit } from '@angular/core';
import { Translate } from '../interfaces/translate';
import { LanguageService } from '../services/language.service';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.css']
})
export class EmployeeComponent implements OnInit {

  translate: Translate;

  constructor(private languageService: LanguageService) {
    this.translate = languageService.loadLanguage();
   }

  ngOnInit(): void {
  }

}
