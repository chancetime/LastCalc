package com.lastcalc.db;

import java.io.*;


import com.googlecode.objectify.annotation.Unindexed;
import com.lastcalc.TokenList;

public class Line implements Serializable {
	private static final long serialVersionUID = 8234981855606438804L;

	public Line(final String question, final TokenList answer)
			throws IOException {
		this.question = question;
		this.answer = answer;
	}

	public String varAssignment;

	public String question;

	@Unindexed
	public TokenList answer;
}