//
// FPlayAndroid is distributed under the FreeBSD License
//
// Copyright (c) 2013-2014, Carlos Rafael Gimenes das Neves
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//	list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//	this list of conditions and the following disclaimer in the documentation
//	and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.
//
// https://github.com/carlosrafaelgn/FPlayAndroid
//
package br.com.carlosrafaelgn.fplay.list;

import android.content.Context;

import java.io.DataInputStream;
import java.io.InputStream;

public final class ShoutcastRadioStationGenre {
	public final int id;
	public final String name;
	public ShoutcastRadioStationGenre[] children;

	private ShoutcastRadioStationGenre(DataInputStream dataInputStream, byte[] temp) throws Throwable {
		id = dataInputStream.readInt();
		final int len = dataInputStream.readUnsignedShort();
		if (len > temp.length || len != dataInputStream.read(temp, 0, len))
			throw new IllegalArgumentException();
		name = new String(temp, 0, len, "UTF-8");
	}

	public static ShoutcastRadioStationGenre[] loadGenres(Context context) {
		InputStream inputStream = null;
		DataInputStream dataInputStream = null;
		try {
			final byte[] temp = new byte[128];
			inputStream = context.getAssets().open("binary/genres.dat");
			dataInputStream = new DataInputStream(inputStream);
			final  int version = dataInputStream.readUnsignedShort();
			if (version != 1)
				return null;
			final int count = dataInputStream.readUnsignedShort();
			if (count <= 0 || count > 100)
				return null;
			ShoutcastRadioStationGenre[] parents = new ShoutcastRadioStationGenre[count];
			for (int p = 0; p < count; p++) {
				parents[p] = new ShoutcastRadioStationGenre(dataInputStream, temp);
				int childCount = dataInputStream.readUnsignedShort();
				if (childCount <= 0 || childCount > 100)
					continue;
				parents[p].children = new ShoutcastRadioStationGenre[childCount];
				for (int c = 0; c < childCount; c++)
					parents[p].children[c] = new ShoutcastRadioStationGenre(dataInputStream, temp);
			}
			return parents;
		} catch (Throwable ex) {
			return null;
		} finally {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
