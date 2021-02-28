import { FileAttachment } from 'src/app/models/file-attachment';

export class ContactUs {

  subject: string;
  message: string;
  replyEmail: string;
  attachmentList: Array<FileAttachment>;

  constructor(){
    this.attachmentList = new Array();
  }
}
