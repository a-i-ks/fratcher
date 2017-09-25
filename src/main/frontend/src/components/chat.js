import React from "react";

import User from "../util/User";

import axios from "axios";

class Chat extends React.Component {

    constructor(props) {
        super();
        this.state = {
            match: props.match,
            messages: [],
            messageInput: "",
            loading: true,
            error: null
        };

        this.handleMessageChange = this.handleMessageChange.bind(this);
        this.handleKeyPressOnInput = this.handleKeyPressOnInput.bind(this);
        this.submitMessage = this.submitMessage.bind(this);
        this.checkForNewMessages = this.checkForNewMessages.bind(this);
    }

    renderLoading() {
        return <div>Loading...</div>;
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        this.checkForNewMessages();
    }

    renderError() {
        return (
            <div style={{color: 'red'}}>An error has occurred: {this.state.error.message}</div>
        );
    }

    processMsgToDiv(msg) {
        const {message, senderId} = msg;
        if (senderId === User.id) { // msg by currentUser
            return (
                <li style={{width: '100%'}} key={msg.id}>
                    <div className="msj macro">
                        <div className="avatar">
                            <img className="img-circle" style={{width: '100%'}} src={User.profilePic}/>
                        </div>
                        <div className="text text-l">
                            <p>{message}</p>
                            <p>
                                <small>date</small>
                            </p>
                        </div>
                    </div>
                </li>
            )
        } else {
            return (
                <li style={{width: '100%'}} key={msg.id}>
                    <div className="msj-rta macro">
                        <div className="text text-r">
                            <p>{message}</p>
                            <p>
                                <small>date</small>
                            </p>
                        </div>
                        <div className="avatar" style={{padding: '0px 0px 0px 10px !important'}}>
                            <img className="img-circle" style={{width: '100%'}} src="you.avatar"/>
                        </div>
                    </div>
                </li>
            )
        }
    }


    renderMessages() {
        let {messages} = this.state;
        messages.sort(function (m1, m2) {
            return (m1.id - m2.id);
        });
        let messagesDivContent = [];
        if (messages != null && messages.length > 0) {
            messages.map((msg) => {
                messagesDivContent.push(this.processMsgToDiv(msg))
            })
        } else {
            messagesDivContent = <div/>;
        }
        return (messagesDivContent);
    }

    handleMessageChange(event) {
        this.setState({messageInput: event.target.value});
    }

    handleKeyPressOnInput(event) {
        if (event.key === 'Enter') {
            this.submitMessage()
        }
    }

    submitMessage() {
        const {id} = this.state.match;
        axios.post('/api/match/' + id + '/chat/message',
            {
                message: this.state.messageInput
            }, {
                validateStatus: (status) => {
                    return (status >= 200 && status < 300) || status === 401
                }
            })
            .then((response) => {
                if (response.status === 201) {
                    console.log("status 201");
                    this.checkForNewMessages();
                } else {
                    throw new Error("Unexpected response with code " + response.status);
                }
            })
            .catch(({err}) => {
                if (err) {
                    console.error("Error during submit of message:");
                    console.error(err);
                    this.setState({
                        loading: false,
                        error: err.message
                    });
                }
            });
    }

    checkForNewMessages() {
        const {id} = this.state.match;
        axios.get('/api/match/' + id + '/chat', {
            validateStatus: (status) => {
                return (status >= 200 && status < 300) || status === 404
            }
        })
            .then(({data, status}) => {
                if (status === 404) {
                    console.log("[404] ChatConversation not existing");
                    // delete saved messages
                    let updated = this.state.messages.slice();
                    updated.push("");
                    this.setState({
                        messages: updated,
                        loading: false,
                        error: null
                    });
                } else if (status === 200) {
                    if (!data.messages) {
                        throw new Error("Messages array was empty");
                    }
                    this.setState({
                        messages: data.messages,
                        loading: false,
                        error: null
                    });
                } else {
                    throw new Error("Received status code " + status)
                }
                this.forceUpdate();
            })
            .catch(({err}) => {
                console.error("Error during load of ChatConversation:");
                console.error(err.message);
                this.setState({
                    loading: false,
                    error: err.message
                });
            });
    }


    render() {
        const {loading} = this.state;

        return (
            <div className="chat">
                {loading ? this.renderLoading() :
                    <div className="chat col-sm-3 col-sm-offset-4 frame">
                        <ul>{this.renderMessages()}</ul>
                        <div>
                            <div className="msj-rta macro" style={{margin: `auto`}}>
                                <div className="text text-r" style={{background: 'whitesmoke !important'}}>
                                    <input className="mytext"
                                           value={this.state.messageInput}
                                           onChange={this.handleMessageChange}
                                           onKeyPress={this.handleKeyPressOnInput}
                                           placeholder="Type a message"/>
                                </div>
                            </div>
                        </div>
                    </div>}
            </div>
        );
    }
}

export default Chat;