import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { async, TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { AppRoutingModule } from '../app-routing.module';
import { ContactUs } from '../models/contact-us';
import { Patterns } from '../models/patterns';
import { HomeService } from './home.service';
import { UserService } from './user.service';

describe('HomeService', () => {
  const patterns = new Patterns();
  let homeService: HomeService;
  let httpMock: HttpTestingController;
  let contactUs: ContactUs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        AppRoutingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [TranslateService, HttpClient, UserService,
        { provide: APP_BASE_HREF, useValue: '/' }
      ]
    });
    homeService = TestBed.inject(HomeService);
    httpMock = TestBed.inject(HttpTestingController);
    contactUs = new ContactUs();
  });

  it('should be created', () => {
    expect(homeService).toBeTruthy();
  });
  it('should call sendEmailByContactUs and return confirmation', async(() => {
    homeService.sendEmailByContactUs(contactUs, 'captchaResponse').subscribe((data: HttpResponse<any>) => {
      expect(data.body).toEqual('confirmation');
      expect(data.status).toEqual(204);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/sendEmailByContactUs');
    const expectedResponse = new HttpResponse({ status: 204, body: 'confirmation' });
    mockReq.event(expectedResponse);
    expect(mockReq.request.method).toEqual('POST');
    expect(mockReq.request.responseType).toEqual('text');
    expect(mockReq.request.body).toEqual(contactUs);
  }));
  it('should return error if sendEmailByContactUs ended defeat', async(() => {
    const errorType = 'cannot sendEmailByContactUs';
    homeService.sendEmailByContactUs(contactUs, 'captchaResponse').subscribe(() => { }, errorResponse => {
      expect(errorResponse.error.type).toEqual(errorType);
    });
    const mockReq = httpMock.expectOne(patterns.baseUrl + '/sendEmailByContactUs');
    mockReq.error(new ErrorEvent(errorType));
  }));
});
