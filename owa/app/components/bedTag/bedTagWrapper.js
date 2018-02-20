import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import _ from 'lodash';

import Header from 'components/header';
import BedTagList from 'components/bedTag/bedTagList';
import AddEditBedTag from 'components/bedTag/bedTagForm/addEditBedTag';
import UrlHelper from 'utilities/urlHelper';
import ReactNotify from 'react-notify';

export default class BedTagWrapper extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.state = {
            bedTags: [],
            activePage: 'listing',
            pageData: {}
        };

        this.urlHelper = new UrlHelper();
        this.initData = this.initData.bind(this);
        this.initData();
    }

    initData() {
        this.bedTagFunctions.fetchBedTags();
    }

    bedTagFunctions = {
        setState: (stateData) => {
            this.setState({
                ...stateData
            });
        },
        getBedTags: () => {
            return this.state.bedTags;
        },
        getBedTagByUuid: (bedTagUuid) => {
            return _.find(this.state.bedTags, function(bedTag) {
                return bedTag.uuid == bedTagUuid;
            });
        },
        fetchBedTags: () => {
            const self = this;
            axios
                .get(this.urlHelper.apiBaseUrl() + '/bedTag', {
                    params: {
                        v: 'full'
                    }
                })
                .then(function(response) {
                    self.setState({
                        bedTags: response.data.results
                    });
                })
                .catch(function(errorResponse) {
                    const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                    self.props.bedTagFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
                });
        },
        notify: (notifyTag, message) => {
            const self = this;
            if (notifyTag == 'success') {
                self.refs.notificator.success('Success', message, 5000);
            } else if (notifyTag == 'error') {
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
                        <BedTagList bedTags={this.state.bedTags} bedTagFunctions={this.bedTagFunctions} />
                    ) : (
                        <AddEditBedTag
                            bedTagFunctions={this.bedTagFunctions}
                            bedTagUuid={this.state.pageData.bedTagUuid}
                            operation={this.state.pageData.operation}
                        />
                    )}
                </div>
            </div>
        );
    }
}

BedTagWrapper.contextTags = {
    store: PropTypes.object
};
