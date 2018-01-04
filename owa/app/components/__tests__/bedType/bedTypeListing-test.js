import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';

import BedTypeList from 'components/bedType/bedTypeList';
import bedTypeFunctionsMock from 'components/__mocks__/bedTypeFunctions-mock';

const testProps = {
    bedTypeFunctions: bedTypeFunctionsMock,
    bedTypes: bedTypeFunctionsMock.getBedTypes()
};

describe('BedTypeList', () => {
    it('Should render bed Type list properly', () =>{
        const bedTypeList = shallow(<BedTypeList bedTypes={testProps.bedTypes} bedTypeFunctions={testProps.bedTypeFunctions}/>);

        expect(bedTypeList.find('BedTypeListRow').length).toBe(3);
        expect(shallowToJson(bedTypeList)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnAddNewHandler = jest.spyOn(BedTypeList.prototype, 'addNewHandler');
        const spyOnSetState = jest.spyOn(testProps.bedTypeFunctions, 'setState');
        const bedTypeList = mount(<BedTypeList bedTypes={testProps.bedTypes} bedTypeFunctions={testProps.bedTypeFunctions}/>);

        bedTypeList.find('button[value=\'Add New\']').simulate('click');
        expect(spyOnAddNewHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTypeId: null
            }
        });
    });
});