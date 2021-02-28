import { browser } from 'protractor';
import { protractor } from 'protractor/built/ptor';
import { EmployeePage } from './employee.po';

describe('workspace-project Employee', () => {
  let page: EmployeePage;


  beforeEach(() => {
    page = new EmployeePage();
  });

  it('should go to login page, display label for login, login as employee, go to self edit page, update own data and logout', () => {
    page.navigateToLoginPage();
    expect(page.getLabelLoginText()).toEqual('Login/email:');
    page.loginAsEmployee('xxxxxxxxxxx', 'xxxxxxxxxxxx');
    const EC = protractor.ExpectedConditions;
    const urlEmployeeChanged = EC.urlContains('/employee')
    browser.wait(urlEmployeeChanged, 5000, 'url should contain /employee');
    page.navigateToSelfEditPage();
    const urlSelfEditChanged = EC.urlContains('/employee/selfedit')
    browser.wait(urlSelfEditChanged, 5000, 'url should contain /employee/selfedit');
    const randomPhone = Math.round(Math.random() * 1000000000);
    page.updateEmployeeData(randomPhone.toString(), 'Employee11', 'Employee11');
    browser.sleep(1000);
    page.confirmAlert();
    expect(page.getUpdatedStreetNo()).toEqual(randomPhone.toString());
    page.logout();
    const url = browser.getCurrentUrl();
    expect(url).toContain('/login');
  });

});
