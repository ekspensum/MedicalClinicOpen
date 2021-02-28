import { Injectable } from '@angular/core';
import { ContactUs } from '../models/contact-us';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MockHomeService {

  constructor() { }

  sendEmailByContactUs(contactUs: ContactUs, captchaResponse: string){
    let userData = new HttpResponse<any>({
      "body": "SENT_EMAIL"
    });
    return of(userData);
  }
}
