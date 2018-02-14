import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import _ from 'lodash';

import Header from 'components/header';
import BedTypeList from 'components/bedType/bedTypeList';
import AddEditBedType from 'components/bedType/bedTypeForm/addEditBedType';
import UrlHelper from 'utilities/urlHelper';
import ReactNotify from 'react-notify';

export default class BedTypeWrapper extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.state = {
            bedTypes: [],
            activePage: 'listing',
            pageData: {}
        };

        this.urlHelper = new UrlHelper();
        this.initData = this.initData.bind(this);
        this.initData();
    }

    initData() {
        this.bedTypeFunctions.fetchBedTypes();
    }

    bedTypeFunctions = {
        setState: (stateData) => {
            this.setState({
                ...stateData
            });
        },
        getBedTypes: () => {
            return this.state.bedTypes;
        },
        getBedTypeByUuid: (bedTypeUuid) => {
            return _.find(this.state.bedTypes, function(bedType) {
                return bedType.uuid == bedTypeUuid;
            });
        },
        getBedTypeName: (bedTypeName) => {
            return _.find(this.state.bedTypes, function(bedType) {
                return bedType.name == bedTypeName;
            });
        },
        fetchBedTypes: () => {
            const self = this;
            axios
                .get(this.urlHelper.apiBaseUrl() + '/bedtype', {
                    params: {
                        v: 'full'
                    }
                })
                .then(function(response) {
                    self.setState({
                        bedTypes: response.data.results
                    });
                })
                .catch(function(error) {
                    self.admissionLocationFunctions.notify('error', error.message);
                });
        },
        notify: (notifyType, message) => {
            const self = this;
            if (notifyType == 'success') {
                self.refs.notificator.success('Success', message, 5000);
            } else if (notifyType == 'error') {
                self.refs.notificator.error('Error', message, 5000);
            } else {
                self.refs.notificator.error('Info', message, 5000);
            }
        }
    };

    style = {
        wrapper: {
            marginTop: 10,
            paddingTop: 20,
            borderRadius: 5,
            backgroundColor: '#fff',
            minHeight: 500
        }
    };

    render() {
        return (
            <div>
                <ReactNotify ref="notificator" />
                <Header path={this.props.match.path} />
                <div style={this.style.wrapper}>
                    {this.state.activePage == 'listing' ? (
                        <BedTypeList bedTypes={this.state.bedTypes} bedTypeFunctions={this.bedTypeFunctions} />
                    ) : (
                        <AddEditBedType
                            bedTypeFunctions={this.bedTypeFunctions}
                            bedTypeUuid={this.state.pageData.bedTypeUuid}
                            operation={this.state.pageData.operation}
                        />
                    )}
                </div>
            </div>
        );
    }
}

BedTypeWrapper.contextTypes = {
    store: PropTypes.object
};
