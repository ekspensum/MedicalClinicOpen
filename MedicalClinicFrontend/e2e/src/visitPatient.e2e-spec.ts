import { browser } from 'protractor';
import { protractor } from 'protractor/built/ptor';
import { PatientVisitPage } from './visitPatient.po';

describe('workspace-project visit Patient', () => {
  let page: PatientVisitPage;
  let visitDateTime: string;

  beforeEach(() => {
    page = new PatientVisitPage();
  });

  it('should go to login page, display label for login, login as patient', () => {
    page.navigateToLoginPage();
    expect(page.getLabelLoginText()).toEqual('Login/email:');
    page.loginAsPatient('xxxxxxxxxxx', 'xxxxxxxxxxxxxx');
    const EC = protractor.ExpectedConditions;
    const urlPatientChanged = EC.urlContains('/patient')
    browser.wait(urlPatientChanged, 5000, 'url should contain /patient');
  });
  it('should go to schedule visit page, schedule visit, go tu cancel visit page, cancel before planned visit and logout', () => {
    const EC = protractor.ExpectedConditions;
    page.navigateToScheduleVisit();
    const urlScheduleVisitChanged = EC.urlContains('/patient/schedulevisit')
    browser.wait(urlScheduleVisitChanged, 5000, 'url should contain /patient/schedulevisit');
    page.selectDoctorForVisit();
    page.selectVisitTimeRadioButton();
    page.getVisitDate().then(date => {
      page.getVisitTime().then(time => {
        visitDateTime = date.slice(0, 10)+time;
        page.scheduleVisit();
        page.confirmOkAlert();
        page.navigateToCancelVisit();
        const urlCancelVisitChanged = EC.urlContains('/patient/cancelvisit')
        browser.wait(urlCancelVisitChanged, 5000, 'url should contain /patient/cancelvisit');
        page.selectCancelVisitRadioButton(visitDateTime);
        page.cancelVisit();
        browser.switchTo().alert().accept();
        page.confirmOkAlert();
        page.logout();
        const url = browser.getCurrentUrl();
        expect(url).toContain('/login');
      });
    });
  });
});
