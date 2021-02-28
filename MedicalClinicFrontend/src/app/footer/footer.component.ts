import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Translate } from '../interfaces/translate';
import { Company } from '../models/company';
import { LanguageService } from '../services/language.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  translate: Translate;
  company: Company;

  constructor(private languageService: LanguageService, private userService: UserService) {
    this.translate = languageService.loadLanguage();
    this.company = new Company();
   }

  ngOnInit(): void {
    this.userService.getCompanyData().subscribe((resp: HttpResponse<any>) => {
      this.company = resp.body;
    });
  }

}
