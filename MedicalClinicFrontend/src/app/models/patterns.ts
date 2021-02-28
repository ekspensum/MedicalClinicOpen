export class Patterns {

  // baseUrl: string = 'http://localhost:8080/api';
  baseUrl: string = 'https://aticode.pl:8444/api';
  login: string = '^[a-zA-Z0-9_@.-]{3,40}$';
  password: string = '^.{4,25}$';
  name: string = '^[^`!#$%\^*+={\\[\\]}|\\\\:"\';<>]{2,25}$';
  smalName: string = '^[^`!#$%\^*+={\\[\\]}|\\\\:"\';<>]{1,10}$';
  smalNameFromZero: string = '^[^`!#$%\^*+={\\[\\]}|\\\\:"\';<>]{0,10}$';
  largeName: string = '^[^`!#$%\^*+={\\[\\]}|\\\\:"\';<>]{2,75}$';
  subject: string = '^[^`$%\^*+={\\[\\]}|\\\\#"\'<>]{2,100}$';
  description: string = '^[^`$%\^*+={\\[\\]}|\\\\#"\'<>]{2,512}$';
  largeDescription: '^[^`$%\^*+={\\[\\]}|\\\\#"\'<>]{5,1024}$';
  email: string = '^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$';
  phone: string = '^$|^[0-9-+()]{9,20}$';
  pesel: string = '^$|^[0-9]{11}$';
  regon: string = '^[0-9]{9}$';
  nip: string = '^[0-9]{10}$';
  zipCode: string = '^[0-9]{2}-[0-9]{3}$';
  maxCompressedFileSize: number = 40000;
  maxOryginalFileSize: number = 6000000;
  attachmentsNumber: number = 10;
  attachmentsSize: number = 10240000;
  latitude: string = '^[0-9.-]{6,12}$';
  longitude: string = '^[0-9.-]{6,14}$';
  searchPatient: string = '^[^`!#$%\^*+={\\[\\]}|\\\\:"\';<>]{3,20}$';
}
