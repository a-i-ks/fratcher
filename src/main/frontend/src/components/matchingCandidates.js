import React from "react";

class MatchingCandidates extends React.Component {

    constructor(props) {
        super();
        this.state = {};

        this.handleClick = this.handleClick.bind(this);
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/matches')
            .then(({data}) => {
                this.setState({
                    matchingCadidates: data
                })
            });
    }

    render() {
        return (
            <div></div>
        );
    }
}

export default MatchList;