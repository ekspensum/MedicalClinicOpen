import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';
import { AddAdminComponent } from './add-admin.component';

describe('AddAdminComponent', () => {
  let component: AddAdminComponent;
  let fixture: ComponentFixture<AddAdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddAdminComponent],
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
      providers: [TranslateService, HttpClient, UserService,
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: UserService, useClass: MockUserService }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('form filed username should be empty and should be name: username', () => {
    const hostElement = fixture.nativeElement;
    const usernameInput: HTMLInputElement = hostElement.querySelector('#username');
    expect(usernameInput.value).not.toBeFalse();
    expect(usernameInput.name).toEqual('username');
  });
  it('after call addEmployee should be return ADDED', () => {
    component.addAdmin();
    expect(component.answer.serverAnswer).toEqual('ADDED');
  });
  it('should call ', () => {
    spyOn(component, 'checkPesel');
    component.checkPesel('pesel');
    expect(component.checkPesel).toHaveBeenCalled();
  });
  it('shuold be correct state onCorrectServerAnswer', () => {
    component.onCorrectServerAnswer();
    expect(component.employee.user.firstName).toEqual('');
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.answer.visableForm).toBeTruthy();
    expect(component.answer.notunique).toBeFalsy;
  });
});
