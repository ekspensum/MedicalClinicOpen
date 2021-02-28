import { browser, by, element } from 'protractor';

export class EmployeeVisitPage {
  login = element(by.css('input[name=login]'));
  password = element(by.css('input[name=password]'));
  searchPatientInput = element(by.css('input[name=searchInput]'));
  submitLogin = element(by.buttonText('Sign in'));
  submitLogout = element(by.buttonText('Logout'));
  submitSearch = element(by.buttonText('Search'));
  submitSelectPatient = element(by.buttonText('Select'));
  submitScheduleVisit = element(by.buttonText('Schedule visit'));
  submitCancelVisit = element(by.buttonText('Delete'));
  submitConfirmationVisit = element(by.buttonText('Visit confirmation'));
  confirm = element(by.buttonText('OK'));

  navigateToLoginPage(): Promise<unknown> {
    return browser.get('/login') as Promise<unknown>;
  }
  navigateToScheduleVisit(): Promise<unknown> {
    return browser.get('/employee/schedulevisit') as Promise<unknown>;
  }
  navigateToConfirmVisit(): Promise<unknown> {
    return browser.get('/employee/visitconfirmation') as Promise<unknown>;
  }
  navigateToCancelVisit(): Promise<unknown> {
    return browser.get('/employee/cancelvisit') as Promise<unknown>;
  }
  getLabelLoginText(): Promise<string> {
    return element(by.css('app-login label[for=login]')).getText() as Promise<string>;
  }
  loginAsEmployee(login: string, password: string) {
    this.login.sendKeys(login);
    this.password.sendKeys(password);
    this.submitLogin.click();
  }
  searchPatient(){
    this.searchPatientInput.clear();
    this.searchPatientInput.sendKeys('79060240213');
    this.submitSearch.click();
  }
  searchVisitToConfirmation(){
    this.submitSearch.click();
  }
  selectPatientRadioButton(){
    element(by.id('79060240213')).click();
    this.submitSelectPatient.click();
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
  selectConfirmationVisitRadioButton(visitDateTime: string){
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
  confirmationVisit(){
    this.submitConfirmationVisit.click();
  }
  confirmAlert(){
    this.confirm.click();
  }
  logout(){
    this.submitLogout.click();
  }

}
