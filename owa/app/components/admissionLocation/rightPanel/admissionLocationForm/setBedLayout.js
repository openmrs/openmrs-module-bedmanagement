import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./admissionLocationForm.css');
export default class SetBedLayout extends React.Component {
    constructor(props, context) {
        super(props, context);
        this.initData = this.initData.bind(this);
        this.initData();

        this.state = {
            locationUuid: this.admissionLocation.uuid,
            row: props.row,
            column: props.column,
            disableSubmit: false,
            rowFieldError: '',
            columnFieldError: ''
        };

        this.intl = context.intl;
        this.urlHelper = new UrlHelper();
        this.onChangeColumnField = this.onChangeColumnField.bind(this);
        this.onChangeRowField = this.onChangeRowField.bind(this);
        this.cancelEventHandler = this.cancelEventHandler.bind(this);
        this.onSubmitHandler = this.onSubmitHandler.bind(this);
    }

    componentWillUpdate(nextProps, nextState) {
        this.initData();
    }

    initData() {
        this.admissionLocation = this.props.admissionLocationFunctions.getAdmissionLocationByUuid(
            this.props.activeUuid
        );
    }

    onChangeRowField() {
        if (this.rowField.value >= 1) {
            this.setState({
                rowFieldError: '',
                row: Number(this.rowField.value)
            });
        } else {
            let errorMsg = '';
            if (this.rowField.value == '') {
                errorMsg = this.intl.formatMessage({id: 'ROW_REQUIRED_MSG'});
            } else if (this.rowField.value <= 0) {
                errorMsg = this.intl.formatMessage({id: 'ROW_SHOULD_GREATER_THAN_ZERO'});
            }

            this.setState({
                rowFieldError: errorMsg,
                row: this.rowField.value
            });
        }
    }

    onChangeColumnField() {
        if (this.columnField.value >= 1 && this.columnField.value <= 10) {
            this.setState({
                columnFieldError: '',
                column: Number(this.columnField.value)
            });
        } else {
            let errorMsg = '';
            if (this.columnField.value == '') {
                errorMsg = this.intl.formatMessage({id: 'COLUMN_REQUIRED_MSG'});
            } else if (this.columnField.value <= 0 || this.columnField.value > 10) {
                errorMsg = this.intl.formatMessage({id: 'COLUMN_SHOULD_GREATER_THAN_ZERO'});
            }

            this.setState({
                columnFieldError: errorMsg,
                column: this.columnField.value
            });
        }
    }

    cancelEventHandler(event) {
        event.preventDefault();
        this.props.admissionLocationFunctions.setState({
            activePage: 'listing',
            pageData: {},
            activeUuid: this.props.activeUuid
        });
    }

    onSubmitHandler(event) {
        event.preventDefault();
        if (this.state.rowFieldError != '' || this.state.columnFieldError != '') {
            const errorMsg = this.intl.formatMessage({id: 'FIX_ERROR_MSG'});
            this.props.admissionLocationFunctions.notify('error', errorMsg);
            return;
        }

        this.setState({
            disableSubmit: true
        });
        const self = this;
        const parameters = {
            bedLayout: {
                row: this.state.row,
                column: this.state.column
            }
        };

        axios({
            method: 'post',
            url: this.urlHelper.apiBaseUrl() + '/admissionLocation/' + this.state.locationUuid,
            params: {
                v: 'layout'
            },
            headers: {'Content-Type': 'application/json'},
            data: parameters
        })
            .then(function(response) {
                self.setState({
                    disableSubmit: false
                });
                const sussessMsg = self.intl.formatMessage({id: 'BED_LAYOUT_SAVE_MSG'});
                self.props.admissionLocationFunctions.notify('success', sussessMsg);
                self.props.admissionLocationFunctions.setState({
                    activePage: 'listing',
                    activeUuid: self.props.activeUuid
                });
            })
            .catch(function(errorResponse) {
                self.setState({
                    disableSubmit: false
                });
                console.log(errorResponse);
                const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                self.props.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    render() {
        return (
            <div className="main-container">
                <fieldset className="admission-location-form">
                    <legend>&nbsp; {this.intl.formatMessage({id: 'SET_LAYOUT'})} &nbsp;</legend>
                    <div className="block-content">
                        <form onSubmit={this.onSubmitHandler}>
                            <div className="form-block">
                                <label className="form-title inline">
                                    {this.intl.formatMessage({id: 'LOCATION'})}:
                                </label>
                                <span>{this.admissionLocation.name}</span>
                            </div>
                            <div className="form-block">
                                <label className="form-title">{this.intl.formatMessage({id: 'ROWS'})}:</label>
                                <input
                                    type="number"
                                    value={this.state.row}
                                    ref={(input) => {
                                        this.rowField = input;
                                    }}
                                    required={true}
                                    onChange={this.onChangeRowField}
                                    id="row-field"
                                />
                                {this.state.rowFieldError != '' ? (
                                    <p className="error">{this.state.rowFieldError}</p>
                                ) : (
                                    ''
                                )}
                            </div>
                            <div className="form-block">
                                <label className="form-title">{this.intl.formatMessage({id: 'COLUMNS'})}:</label>
                                <input
                                    type="number"
                                    value={this.state.column}
                                    ref={(input) => {
                                        this.columnField = input;
                                    }}
                                    required={true}
                                    onChange={this.onChangeColumnField}
                                    id="column-field"
                                />
                                {this.state.columnFieldError != '' ? (
                                    <p className="error">{this.state.columnFieldError}</p>
                                ) : (
                                    ''
                                )}
                            </div>
                            <div className="form-block">
                                <input
                                    type="submit"
                                    name="submit"
                                    value={
                                        this.state.disableSubmit
                                            ? this.intl.formatMessage({id: 'SAVING'})
                                            : this.intl.formatMessage({id: 'SAVE'})
                                    }
                                    disabled={this.state.disableSubmit}
                                    className="form-btn float-left margin-right"
                                />
                                <input
                                    type="button"
                                    onClick={this.cancelEventHandler}
                                    name="cancel"
                                    value={this.intl.formatMessage({id: 'CANCEL'})}
                                    className="form-btn float-left"
                                />
                            </div>
                        </form>
                    </div>
                </fieldset>
            </div>
        );
    }
}

SetBedLayout.propTypes = {
    row: PropTypes.number,
    column: PropTypes.number,
    activeUuid: PropTypes.string,
    admissionLocationFunctions: PropTypes.object.isRequired
};

SetBedLayout.contextTypes = {
    intl: PropTypes.object
};
