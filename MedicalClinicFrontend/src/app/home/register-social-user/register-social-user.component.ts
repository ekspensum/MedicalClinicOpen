import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Translate } from 'src/app/interfaces/translate';
import { AuthenticationRequest } from 'src/app/models/authentication-request';
import { Patient } from 'src/app/models/patient';
import { Patterns } from 'src/app/models/patterns';
import { User } from 'src/app/models/user';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-register-social-user',
  templateUrl: './register-social-user.component.html',
  styleUrls: ['./register-social-user.component.css']
})
export class RegisterSocialUserComponent implements OnInit {

  user: User;
  patient: Patient;
  translate: Translate;
  patterns: Patterns;
  answer: UserServiceAnswer;
  languageCodeList: Array<string>;
  reCaptchaVerify: boolean;
  reCaptchaResponse: string;
  isValidPesel: boolean;
  photoUrl: string;
  authenticationRequest: AuthenticationRequest;

  constructor(private userService: UserService, private languageService: LanguageService, private authService: AuthenticationService,
              private translateService: TranslateService) {
    this.patterns = new Patterns();
    this.user = new User();
    this.patient = new Patient(this.user);
    this.translate = languageService.loadLanguage();
    this.languageCodeList = languageService.serveLanguages;
    this.patient.user.language = this.translateService.getBrowserLang();
    this.patient.socialUserId = authService.socialUser.id
    this.patient.user.username = authService.socialUser.email;
    this.patient.user.firstName = authService.socialUser.firstName;
    this.patient.user.lastName = authService.socialUser.lastName;
    this.patient.user.email = authService.socialUser.email;
    this.photoUrl = authService.socialUser.photoUrl;
    this.convertImageToBase64(this.photoUrl);
    this.answer = new UserServiceAnswer();
  }

  ngOnInit(): void {
  }

  addSocialUser(): void {
    this.userService.saveSocialUser(this.patient, this.reCaptchaResponse).subscribe(
      (response: HttpResponse<any>) => {
        this.answer = this.userService.switchAnswer(response.body);
        if(this.answer.serverAnswer === 'ADDED' || this.answer.serverAnswer === 'UPDATED'){
          this.authService.authenticateSocialUser(this.authService.socialUser);
        }
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }
  resolvedReCaptcha(captchaResponse: string): void {
    this.reCaptchaResponse = captchaResponse;
    this.reCaptchaVerify = true;
  }
  checkPeselAndSetGender(pesel: string): void {
    this.isValidPesel = this.userService.decodePesel(pesel).valid;
    this.patient.gender = this.userService.decodePesel(pesel).gender;
  }
  convertImageToBase64(url): void {
    const xhr = new XMLHttpRequest();
    xhr.onload = (x) => {
      const fileReader = new FileReader();
      fileReader.onload = (e) => {
        this.patient.user.photoString = fileReader.result;
      };
      fileReader.readAsDataURL(xhr.response);
    };
    xhr.open('GET', url);
    xhr.responseType = 'blob';
    xhr.send();
  }

}
