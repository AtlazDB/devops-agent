package br.com.fatec.devopsagent.agent;

public record Observation(Action action, boolean success, String content) { }