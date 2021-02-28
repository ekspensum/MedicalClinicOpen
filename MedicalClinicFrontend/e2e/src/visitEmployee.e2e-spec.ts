import { browser } from 'protractor';
import { protractor } from 'protractor/built/ptor';
import { EmployeeVisitPage } from './visitEmployee.po';

describe('workspace-project visit Employee', () => {
  let page: EmployeeVisitPage;
  let visitDateTime: string;

  beforeEach(() => {
    page = new EmployeeVisitPage();
  });

  it('should go to login page, display label for login, login as employee', () => {
    page.navigateToLoginPage();
    expect(page.getLabelLoginText()).toEqual('Login/email:');
    page.loginAsEmployee('xxxxxxxxxxx', 'xxxxxxxxxxxxxxx');
    const EC = protractor.ExpectedConditions;
    const urlEmployeeChanged = EC.urlContains('/employee')
    browser.wait(urlEmployeeChanged, 5000, 'url should contain /employee');
  });
  it('should go to schedule visit page, search patient, select doctor, schedule visit, go to visit confirmation page, confirm visit, go to cancel visit page, cancel visit and logout', () => {
    const EC = protractor.ExpectedConditions;
    page.navigateToScheduleVisit();
    const urlScheduleVisitChanged = EC.urlContains('/employee/schedulevisit')
    browser.wait(urlScheduleVisitChanged, 5000, 'url should contain /employee/schedulevisit');
    page.searchPatient();
    page.selectPatientRadioButton();
    page.selectDoctorForVisit();
    page.selectVisitTimeRadioButton();
    page.getVisitDate().then(date => {
      page.getVisitTime().then(time => {
        visitDateTime = date.slice(0, 10)+time;
        page.scheduleVisit();
        page.confirmAlert();
        page.navigateToConfirmVisit();
        const urlConfirmVisitChanged = EC.urlContains('/employee/visitconfirmation')
        browser.wait(urlConfirmVisitChanged, 5000, 'url should contain /employee/visitconfirmation');
        page.searchVisitToConfirmation();
        page.selectConfirmationVisitRadioButton(visitDateTime);
        page.confirmationVisit();
        page.confirmAlert();
        page.navigateToCancelVisit();
        const urlCancelVisitChanged = EC.urlContains('/employee/cancelvisit')
        browser.wait(urlCancelVisitChanged, 5000, 'url should contain /employee/cancelvisit');
        page.searchPatient();
        page.selectPatientRadioButton();
        page.selectCancelVisitRadioButton(visitDateTime);
        page.cancelVisit();
        browser.switchTo().alert().accept();
        page.confirmAlert();
        page.logout();
        const url = browser.getCurrentUrl();
        expect(url).toContain('/login');
      });
    });
  });
});
