import { RegistrationModule } from './registration.module';

describe('RegistrationModule', () => {
  let registrationModule: RegistrationModule;

  beforeEach(() => {
    registrationModule = new RegistrationModule();
  });

  it('should create an instance', () => {
    expect(registrationModule).toBeTruthy();
  });
});
