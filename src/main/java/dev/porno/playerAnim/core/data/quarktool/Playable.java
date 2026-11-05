package dev.porno.playerAnim.core.data.quarktool;

public interface Playable {
   int playForward(int time) throws QuarkParsingError;

   int playBackward(int time) throws QuarkParsingError;
}
