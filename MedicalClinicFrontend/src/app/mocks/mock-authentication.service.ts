import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { SocialUser } from 'angularx-social-login';
import { AuthenticationRequest } from '../models/authentication-request';
import { Patterns } from '../models/patterns';
import { UserServiceAnswer } from '../models/user-service-answer';
import { UserService } from '../services/user.service';

@Injectable({
  providedIn: 'root'
})
export class MockAuthenticationService {

  patterns: Patterns;
  helper: JwtHelperService;
  answer: UserServiceAnswer;
  socialUser: SocialUser;
  authenticationRequest: AuthenticationRequest;

  constructor(private http: HttpClient, private router: Router, public userService: UserService) {
    this.patterns = new Patterns();
    this.helper = new JwtHelperService();
    this.answer = new UserServiceAnswer();
    this.authenticationRequest = new AuthenticationRequest();
    this.socialUser = new SocialUser();
  }

  authenticateRegisteredUser(authenticationRequest: AuthenticationRequest) {

  }
  logOut() {

  }
  authenticateSocialUser(socialUser: SocialUser) {

  }
  getLoggedUserName() {
    return 'username';
  }
  hasRoleAdmin() {
    return "ROLE_ADMIN";
  }
  hasRoleEmployee() {
    return "ROLE_EMPLOYEE";
  }
  hasRoleDoctor() {
    return "ROLE_DOCTOR";
  }
  hasRoleOwner() {
    return "ROLE_OWNER";
  }
  hasRolePatient() {
    return "ROLE_PATIENT";
  }
  hasRoleTemporary() {
    return "ROLE_TEMPORARY";
  }
}
