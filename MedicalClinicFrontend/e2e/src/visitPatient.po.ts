import { browser, by, element } from 'protractor';

export class PatientVisitPage {
  login = element(by.css('input[name=login]'));
  password = element(by.css('input[name=password]'));
  submitLogin = element(by.buttonText('Sign in'));
  submitLogout = element(by.buttonText('Logout'));
  submitScheduleVisit = element(by.buttonText('Schedule visit'));
  submitCancelVisit = element(by.buttonText('Delete'));
  confirmOK = element(by.buttonText('OK'));

  navigateToLoginPage(): Promise<unknown> {
    return browser.get('/login') as Promise<unknown>;
  }
  navigateToScheduleVisit(): Promise<unknown> {
    return browser.get('/patient/schedulevisit') as Promise<unknown>;
  }
  navigateToCancelVisit(): Promise<unknown> {
    return browser.get('/patient/cancelvisit') as Promise<unknown>;
  }
  getLabelLoginText(): Promise<string> {
    return element(by.css('app-login label[for=login]')).getText() as Promise<string>;
  }
  loginAsPatient(login: string, password: string) {
    this.login.sendKeys(login);
    this.password.sendKeys(password);
    this.submitLogin.click();
  }
  selectDoctorForVisit(){
    element(by.cssContainingText('option', 'Jan Lubelski')).click();
  }
  selectVisitTimeRadioButton(){
    element(by.id('53')).click();
  }
  selectCancelVisitRadioButton(visitDateTime: string){
    element(by.id(visitDateTime)).click();
  }
  getVisitTime(){
    return element(by.id('visitTime53')).getText();
  }
  getVisitDate(){
    return element(by.id('visitDate5')).getText();
  }
  scheduleVisit(){
    this.submitScheduleVisit.click();
  }
  cancelVisit(){
    this.submitCancelVisit.click();
  }
  confirmOkAlert(){
    this.confirmOK.click();
  }
  logout(){
    this.submitLogout.click();
  }

}
