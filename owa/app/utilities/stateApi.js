export default class StateApi {
    constructor(app) {
        this.app = app;
        this.contextData = {};
    }

    getContextData = () =>{
        return this.contextData;
    };

    getContextDataByKey = (key) => {
        return typeof this.contextData[key] !== 'undefined'? this.contextData[key]: null;
    };

    setContextData = (contexData) => {
        Object.assign(this.contextData, contexData);
    };

    setState = (stateChange) => {
        this.app.setState({
            ...stateChange
        });
    };

    getState = () => {
        return this.app.state;
    };

    getStateByKey = (key) => {
        return typeof this.app.state[key] !=='undefined'? this.app.state[key] : null;
    };
}