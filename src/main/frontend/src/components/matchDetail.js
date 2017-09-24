import React from "react";

import User from "../util/User";
import InterestsTagCloud from "./interestsTagCloud"


import axios from "axios";
import UserAvatar from "react-user-avatar";


class MatchDetail extends React.Component {

    constructor(props) {
        console.log("MatchDetail");
        super();
        this.state = {
            matchIdFromPath: props.match.params.id,
            match: null,
            loading: true,
            error: null
        };
        // this.handleClick = this.handleClick.bind(this);
    }

    renderLoading() {
        return <div>Loading...</div>;
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/match/' + this.state.matchIdFromPath)
            .then(({data}) => {
                console.log("then ..");
                console.log(data);

                let user = null;
                const {user1, user2} = data;
                if (user1.id === User.id) {
                    user = user2;
                } else {
                    user = user1;
                }

                console.log("user=");
                console.log(user);

                this.setState({
                    match: data,
                    user: user,
                    loading: false,
                    error: null
                });
                console.log("after set");
                this.forceUpdate();
            })
            .catch(({error}) => {
                console.log("error during load");
                this.setState({
                    loading: false,
                    error: error
                });
            });
    }

    renderError() {
        return (
            <div>
                An error has occurred: {this.state.error.message}
            </div>
        );
    }

    renderMatch() {
        // Destructuring error and matches from state.
        // => I don't have to write "this.state...." every time.
        const {error, user} = this.state;

        if (error) {
            return this.renderError();
        }



        return (
            <div>
                <div>
                    {user.profile.imgPath &&
                    <UserAvatar size="100" name={user.profile.name} src={user.profile.imgPath}/>}
                    {!user.profile.imgPath &&
                    <UserAvatar size="40" name={user.profile.name}/>}
                </div>
                <h1>{user.profile.name}</h1>
                <div>{user.profile.aboutMe}</div>
                <div>
                    <InterestsTagCloud data={user.profile.interests}/>
                </div>
            </div>
        );
    }

    render() {
        const {loading} = this.state;
        return (
            <div className="component">
                {loading ? this.renderLoading() :
                    <div className="row">
                        <div className="col-sm-6">{this.renderMatch()}</div>
                        <div className="col-sm-6">Chat</div>
                    </div>}
            </div>)
    }
}

export default MatchDetail;