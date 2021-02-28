import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { NgxImageCompressService } from 'ngx-image-compress';
import { MockAuthenticationService } from 'src/app/mocks/mock-authentication.service';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { Employee } from 'src/app/models/employee';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { SelfEditAdminComponent } from './self-edit-admin.component';

describe('SelfEditAdminComponent', () => {
  let component: SelfEditAdminComponent;
  let fixture: ComponentFixture<SelfEditAdminComponent>;
  let authService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelfEditAdminComponent ],
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
        { provide: APP_BASE_HREF, useValue : '/' },
        { provide: UserService, useClass: MockUserService },
        NgxImageCompressService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfEditAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.inject(MockAuthenticationService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should be correct admin/employee username from MockUserService', async(() => {
    fixture.whenStable().then(() => {
      const userNameInput: HTMLInputElement = fixture.nativeElement.querySelector('#username');
      expect(component.employee.user.username).toEqual(userNameInput.value);
    });
  }));
  it('should call checkUsernameAndUpdate', ()=> {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(component, 'checkUsernameAndUpdate');
    component.checkUsernameAndUpdate();
    expect(component.checkUsernameAndUpdate).toHaveBeenCalled();
    expect(authService.logOut).toBeDefined();
  });
  it('after call updateEmployee should be return UPDATED', ()=> {
    component.updateEmployee();
    expect(component.answer.serverAnswer).toEqual('UPDATED');
  });
  it('should be correct state onCorrectUpdate', () => {
    component.onCorrectUpdate();
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.answer.visableForm).toBeTruthy();
    expect(component.answer.notunique).toBeFalsy;
    expect(component.employee.user.photoString).toBeNull();
    expect(component.fileAttachment).toBeNull();
  });
  it('tracks that function was called and tracks the argument of its call', () => {
    let eventFile = new Event('input');
    spyOn(component, 'onFileSelect');
    component.onFileSelect(eventFile);
    expect(component.onFileSelect).toHaveBeenCalled();
    expect(component.onFileSelect).toHaveBeenCalledWith(eventFile);
  });
  it('shuold be correct state onClearFileAttachment', () => {
    let user = new User();
    user.photo = null;
    component.employee = new Employee(user);
    component.onClearFileAttachment();
    expect(component.fileAttachment).toBeNull();
    expect(component.employee.user.photoString).toBeNull();
    expect(component.user_photo).toBeNull();
    user.photo = 'photo';
    component.onClearFileAttachment();
    expect(component.user_photo).toEqual('data:image/png;base64,'+user.photo);
  });
});
