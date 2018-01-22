import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./admissionLocationForm.css');
export default class SetBedLayout extends React.Component {
    constructor(props) {
        super(props);
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
                errorMsg = 'Row is required field';
            } else if (this.rowField.value <= 0) {
                errorMsg = 'Row value should be greater than 0';
            }

            this.setState({
                rowFieldError: errorMsg,
                row: this.rowField.value
            });
        }
    }

    onChangeColumnField() {
        if (this.columnField.value >= 1 && this.columnField.value <= 8) {
            this.setState({
                columnFieldError: '',
                column: Number(this.columnField.value)
            });
        } else {
            let errorMsg = '';
            if (this.columnField.value == '') {
                errorMsg = 'Column is required field';
            } else if (this.columnField.value <= 0 || this.columnField.value > 8) {
                errorMsg = 'Column value should be greater than 0 and less than 9';
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
            this.props.admissionLocationFunctions.notify('error', 'Fix error before submit');
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
                self.props.admissionLocationFunctions.notify(
                    'success',
                    'Admission location bed layout save successfully'
                );
                self.props.admissionLocationFunctions.setState({
                    activePage: 'listing',
                    activeUuid: self.props.activeUuid
                });
            })
            .catch(function(errorResponse) {
                self.setState({
                    disableSubmit: false
                });
                const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                self.props.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    render() {
        return (
            <div className="main-container">
                <fieldset className="admission-location-form">
                    <legend>&nbsp; Set Layout &nbsp;</legend>
                    <div className="block-content">
                        <form onSubmit={this.onSubmitHandler}>
                            <div className="form-block">
                                <label className="form-title inline">Location:</label>
                                <span>{this.admissionLocation.name}</span>
                            </div>
                            <div className="form-block">
                                <label className="form-title">Rows:</label>
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
                                <label className="form-title">Column:</label>
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
                                    value={this.state.disableSubmit ? 'Saving...' : 'Save'}
                                    disabled={this.state.disableSubmit}
                                    className="form-btn float-left margin-right"
                                />
                                <input
                                    type="button"
                                    onClick={this.cancelEventHandler}
                                    name="cancel"
                                    value="Cancel"
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
    activeUuid: PropTypes.string,
    admissionLocationFunctions: PropTypes.object.isRequired
};
