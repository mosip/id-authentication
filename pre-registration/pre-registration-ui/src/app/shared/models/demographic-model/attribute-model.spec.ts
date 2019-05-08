import { AttributeModel } from './attribute.modal';

describe('AttributeModel interface check', () => {
  it('should conform to Attribute interface', () => {
    const attributeModel = new AttributeModel('fre', 'franch');

    expect(typeof attributeModel.language).toEqual('string');
  });
});
