package stickman.engine;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

public class Sound {

	Map<String, Integer> buffers = new LinkedHashMap<>();
	List<Integer> sources = new ArrayList<>();
	List<Float> lengths = new ArrayList<>();

	private float time;
	private int music = -1;
	private boolean end;

	public void playSound(String path, float volume, float length) {
		if (!end) {
			int buffer;
			if (buffers.containsKey(path)) {
				buffer = buffers.get(path);
			} else {
				if (buffers.size() == 256) {
					Map.Entry<String, Integer> entry = buffers.entrySet().iterator().next();
					String key = entry.getKey();
					alDeleteBuffers(buffers.get(key));
					buffers.remove(key);
				}
				buffer = alGenBuffers();
				buffers.put(path, buffer);
				try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
					ShortBuffer pcm = readVorbis(path, 32 * 1024, info);
					alBufferData(buffer, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm,
							info.sample_rate());
				}
			}

			if (sources.size() == 256) {
				alDeleteSources(sources.get(0));
				alSourceStop(sources.get(0));
				sources.remove(0);
				lengths.remove(0);
			}
			int source = alGenSources();

			sources.add(source);
			lengths.add(time + length);
			alSourcei(source, AL_BUFFER, buffer);
			alSourcef(source, AL_GAIN, volume);
			alSourcePlay(source);
		}
	}

	public void playMusic(String path, float volume) {
		if (music != -1) {
			alSourceStop(music);
			alDeleteSources(music);
		}

		int buffer = alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			ShortBuffer pcm = readVorbis(path, 32 * 1024, info);
			alBufferData(buffer, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
		}

		int source = alGenSources();
		music = source;
		alSourcei(source, AL_BUFFER, buffer);
		alSourcef(source, AL_GAIN, volume);
		alSourcei(source, AL_LOOPING, 1);
		alSourcePlay(source);
	}

	public void endSound(String path) {
		for (int i = 0; i < lengths.size(); i++) {
			alSourceStop(sources.get(i));
			alDeleteSources(sources.get(i));
		}

		sources.clear();
		lengths.clear();

		alSourceStop(music);
		alDeleteSources(music);
		end = true;

		int buffer = alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			ShortBuffer pcm = readVorbis(path, 32 * 1024, info);
			alBufferData(buffer, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
		}

		int source = alGenSources();
		alSourcei(source, AL_BUFFER, buffer);
		alSourcef(source, AL_GAIN, 1);
		alSourcePlay(source);
	}

	public void update(float time) {
		this.time = time;
		for (int i = 0; i < lengths.size(); i++) {
			if (lengths.get(i) < time) {
				alSourceStop(sources.get(i));
				alDeleteSources(sources.get(i));
				sources.remove(i);
				lengths.remove(i);
				i--;
			}
		}
	}

	private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) {
		ByteBuffer vorbis;
		try {
			vorbis = ioResourceToByteBuffer(resource, bufferSize);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		IntBuffer error = BufferUtils.createIntBuffer(1);
		long decoder = stb_vorbis_open_memory(vorbis, error, null);

		stb_vorbis_get_info(decoder, info);

		int channels = info.channels();

		int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

		ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);

		pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
		stb_vorbis_close(decoder);

		return pcm;
	}

	private ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws Exception {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1) {

				}
			}
		} else {
			try (InputStream source = Class.forName(Sound.class.getName()).getResourceAsStream(resource);
					ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = BufferUtils.createByteBuffer(bufferSize);

				while (true) {
					int bytes = rbc.read(buffer);
					if (bytes == -1) {
						break;
					}
					if (buffer.remaining() == 0) {
						buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2);
					}
				}
			}
		}

		buffer.flip();
		return buffer.slice();
	}

	private ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
}
