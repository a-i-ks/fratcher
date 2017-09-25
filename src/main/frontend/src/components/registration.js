import React from "react";
import axios from "axios";

import {Link} from "react-router-dom";
import {translate} from "react-i18next";


class Registration extends React.Component {

    constructor(props) {
        super();
        this.state = {
            error: undefined,
            errorText: '',
            btnRegisterDisabled: true,
            name: '',
            nameIsValid: true,
            email: '',
            emailIsValid: true,
            username: '',
            usernameIsValid: true,
            password: '',
            passwordIsValid: true,
            passwordConfirmation: '',
            passwordsAreEqual: true,
            profile: {
                name: ''
            }
        };
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleNameChangeEnd = this.handleNameChangeEnd.bind(this);
        this.handleEmailChange = this.handleEmailChange.bind(this);
        this.handleEmailChangeEnd = this.handleEmailChangeEnd.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handleUsernameChangeEnd = this.handleUsernameChangeEnd.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handlePasswordChangeEnd = this.handlePasswordChangeEnd.bind(this);
        this.handlePasswordConfirmationChange = this.handlePasswordConfirmationChange.bind(this);
        this.handlePasswordConfirmationChangeEnd = this.handlePasswordConfirmationChangeEnd.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleNameChange(event) {
        this.setState({
            profile: Object.assign({}, this.state.profile, {
                name: event.target.value,
            }),
        });
    }

    handleNameChangeEnd(event) {
        if (!this.validateName(this.state.profile.name)) {
            console.warn("Invalid name: " + this.state.profile.name);
            this.setState({nameIsValid: false});
        } else {
            this.setState({nameIsValid: true});
        }
        this.updateRegisterBtnState();
    }

    handleEmailChange(event) {
        this.setState({email: event.target.value.trim()});
    }

    handleEmailChangeEnd(event) {
        if (!this.validateEmail(this.state.email)) {
            console.warn("Invalid email: " + this.state.email);
            this.setState({emailIsValid: false});
        } else {
            this.setState({emailIsValid: true});
        }
        this.updateRegisterBtnState();
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value.trim()});
    }

    handleUsernameChangeEnd(event) {
        if (!this.validateUsername(this.state.username)) {
            console.warn("Invalid username: " + this.state.username);
            this.setState({usernameIsValid: false});
        } else {
            this.setState({usernameIsValid: true});
        }
        this.updateRegisterBtnState();
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value.trim()});
    }

    handlePasswordChangeEnd(event) {
        if (!this.validatePassword(this.state.password)) {
            console.warn("Entered password does not match the requirements");
            this.setState({passwordIsValid: false});
        } else {
            this.setState({passwordIsValid: true});
        }
        this.updateRegisterBtnState();
    }

    handlePasswordConfirmationChange(event) {
        this.setState({passwordConfirmation: event.target.value.trim()});
        if (this.state.password != event.target.value.trim()) {
            this.setState({passwordsAreEqual: false});
        } else {
            this.setState({passwordsAreEqual: true});
        }
        this.updateRegisterBtnState();
    }

    handlePasswordConfirmationChangeEnd(event) {
        if (this.state.password != this.state.passwordConfirmation) {
            console.warn("Passwords do not match");
            this.setState({passwordsAreEqual: false});
        } else {
            this.setState({passwordsAreEqual: true});
        }
        this.updateRegisterBtnState();
    }


    handleSubmit(event) {
        event.preventDefault();
        axios.post('/api/user', this.state, {
            // We allow a status code of 401 (unauthorized). Otherwise it is interpreted as an error and we can't
            // check the HTTP status code.
            validateStatus: (status) => {
                return (status >= 200 && status < 300) || status === 400 || status === 409
            }
        })
            .then(({data, status}) => {
                switch (status) {
                    case 201:
                        this.setState({error: undefined});

                        // Redirect to front page.
                        this.props.history.push("/");
                        break;

                    case 400:
                        this.setState({error: true});
                        this.setState({errorText: data});
                        break;

                    case 409:
                        this.setState({error: true});
                        this.setState({errorText: "Username or E-Mail is already in use."});
                        break;
                }
            });
    }

    validateAllInputFields() {
        if (!this.validateName(this.state.profile.name)) {
            return false;
        }
        if (!this.validateEmail(this.state.email)) {
            return false;
        }
        if (!this.validateUsername(this.state.username)) {
            return false;
        }
        if (this.state.password != this.state.passwordConfirmation) {
            return false;
        }
        return this.validatePassword(this.state.password);

    }

    updateRegisterBtnState() {
        if (this.validateAllInputFields()) {
            this.setState({btnRegisterDisabled: false});
        } else {
            this.setState({btnRegisterDisabled: true});
        }
    }

    validateName(value) {
        // regex from https://stackoverflow.com/a/2044909/3898604
        let re = /^([ \u00c0-\u01ffa-zA-Z'\-])+$/g;
        return re.test(value);
    }

    validateEmail(value) {
        // regex from http://stackoverflow.com/questions/46155/validate-email-address-in-javascript
        let re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(value);
    }

    validateUsername(value) {
        let re = /^[a-zA-z_0-9]{3,20}$/;
        return re.test(value);
    }

    validatePassword(value) {
        let re = /^.{8,}$/;
        return re.test(value);
    }

    render() {
        const {t} = this.props;
        return (
            <div className="registerComp">
                <div className="container">
                    <div className="row main">
                        <div className="panel-heading">
                            <div className="panel-title text-center">
                                <h1 className="title">{t('joinUsTxt')}</h1>
                                <hr/>
                            </div>
                        </div>
                        <div className="main-login main-center">
                            {this.state.error &&
                            <div className="validation-summary-errors">{this.state.errorText}</div>
                            }
                            <form className="form-horizontal" onSubmit={this.handleSubmit}>
                                <div className="form-group">
                                    <label htmlFor="name" className="cols-sm-2 control-label">Your Name</label>
                                    <div className="cols-sm-10">
                                        <div className="input-group">
                                            <span className="input-group-addon"><i className="fa fa-user fa"
                                                                                   aria-hidden="true"/></span>
                                            <input type="text" className="form-control" name="name" id="name"
                                                   placeholder="Enter your Name" value={this.state.profile.name}
                                                   onChange={this.handleNameChange}
                                                   onBlur={this.handleNameChangeEnd}/>
                                        </div>
                                    </div>
                                    {!this.state.nameIsValid &&
                                    <div className="field-validation-error">This name is not valid.</div>
                                    }
                                </div>

                                <div className="form-group">
                                    <label htmlFor="email" className="cols-sm-2 control-label">Your E-Mail</label>
                                    <div className="cols-sm-10">

                                        <div className="input-group">
                                            <span className="input-group-addon"><i className="fa fa-envelope fa"
                                                                                   aria-hidden="true"/></span>
                                            <input type="text" className="form-control" name="email" id="email"
                                                   placeholder="Enter your E-Mail" value={this.state.email}
                                                   onChange={this.handleEmailChange}
                                                   onBlur={this.handleEmailChangeEnd}/>
                                        </div>
                                    </div>
                                    {!this.state.emailIsValid &&
                                    <div className="field-validation-error">This is not a valid email address.</div>
                                    }
                                </div>

                                <div className="form-group">
                                    <label htmlFor="username" className="cols-sm-2 control-label">Username</label>
                                    <div className="cols-sm-10">
                                        <div className="input-group">
                                            <span className="input-group-addon"><i className="fa fa-users fa"
                                                                                   aria-hidden="true"/></span>
                                            <input type="text" className="form-control" name="username" id="username"
                                                   placeholder="Enter your Username" value={this.state.username}
                                                   onChange={this.handleUsernameChange}
                                                   onBlur={this.handleUsernameChangeEnd}/>
                                        </div>
                                    </div>
                                    {!this.state.usernameIsValid &&
                                    <div className="field-validation-error">Invalid username.</div>
                                    }
                                </div>

                                <div className="form-group">
                                    <label htmlFor="password" className="cols-sm-2 control-label">Password</label>
                                    <div className="cols-sm-10">
                                        <div className="input-group">
                                            <span className="input-group-addon"><i className="fa fa-lock fa-lg"
                                                                                   aria-hidden="true"/></span>
                                            <input type="password" className="form-control" name="password"
                                                   id="password" placeholder="Enter your Password"
                                                   value={this.state.password}
                                                   onChange={this.handlePasswordChange}
                                                   onBlur={this.handlePasswordChangeEnd}/>
                                        </div>
                                    </div>
                                    {!this.state.passwordIsValid &&
                                    <div className="field-validation-error">Entered password does not match
                                        requirements.</div>
                                    }
                                </div>

                                <div className="form-group">
                                    <label htmlFor="confirm" className="cols-sm-2 control-label">Confirm
                                        Password</label>
                                    <div className="cols-sm-10">
                                        <div className="input-group">
                                            <span className="input-group-addon"><i className="fa fa-lock fa-lg"
                                                                                   aria-hidden="true"/></span>
                                            <input type="password" className="form-control" name="confirm" id="confirm"
                                                   placeholder="Confirm your Password"
                                                   value={this.state.passwordConfirmation}
                                                   onChange={this.handlePasswordConfirmationChange}
                                                   onBlur={this.handlePasswordConfirmationChangeEnd}/>
                                        </div>
                                    </div>
                                    {!this.state.passwordsAreEqual &&
                                    <div className="field-validation-error">The entered passwords do not match.</div>
                                    }
                                </div>

                                <div className="form-group ">
                                    <button id="btnRegisterSubmit" type="submit"
                                            className="btn btn-primary btn-lg btn-block login-button"
                                            disabled={this.state.btnRegisterDisabled}>
                                        Register
                                    </button>
                                </div>
                                <div className="login-register">
                                    <Link to="/">Login</Link>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default translate()(Registration);