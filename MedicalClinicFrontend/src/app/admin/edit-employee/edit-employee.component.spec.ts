import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';
import { EditEmployeeComponent } from './edit-employee.component';

describe('EditEmployeeComponent', () => {
  let component: EditEmployeeComponent;
  let fixture: ComponentFixture<EditEmployeeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditEmployeeComponent ],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: UserService, useClass: MockUserService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('doctor list length should be 1', () => {
    expect(component.employeeList.length).toEqual(1);
  });
  it('shuold be correct state on assignUserData', ()=> {
    component.selectedIndex = 0;
    component.assignUserData();
    expect(component.userEnabled).toBeFalse();
    expect(component.user_photo).toEqual('data:image/png;base64,some image')
  });
  it('after call resetEmployeePassword server answer should be equal RESET', () => {
    component.selectedIndex = 0;
    component.resetEmployeePassword();
    expect(component.answer.serverAnswer).toEqual('RESET');
  });
  it('after call updateEmployee should be return UPDATED', ()=> {
    component.selectedIndex = 0;
    component.updateEmployee();
    expect(component.answer.serverAnswer).toEqual('UPDATED');
  });
  it('shuold be correct state onCorrectUpdate', ()=> {
    component.onCorrectUpdate()
    expect(component.answer.serverAnswer).toBeNull();
  });
});
